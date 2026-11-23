#!/usr/bin/env python3
"""Traduce README.md (español) a README.en.md (inglés) con la API de Claude.

Envía a Claude el README en español y la versión inglesa actual, para que
reutilice las frases ya traducidas y el diff solo recoja lo que cambió.
Escribe el resultado en README.en.md; la estructura se valida después con
check_readme_sync.py.

Necesita ANTHROPIC_API_KEY en el entorno.
"""
import re
import sys
from pathlib import Path

import anthropic

MODEL = "claude-opus-5-5"
ES, EN = Path("README.md"), Path("README.en.md")

SYSTEM_PROMPT = """You translate the README of PokéManager, an Android app, from Spanish to English \
for its public GitHub repository. The README is the author's portfolio piece, so the English must read \
as natural, professional technical writing, not as a literal translation.

Keep the document structurally identical to the Spanish source, because a CI check compares both files:
- Same headings at the same levels and in the same order. Translate the heading text.
- Same Markdown and HTML, line for line where possible: tables, lists, <div>, <details>, <img> tags and their attributes.
- Image paths, URLs, badge URLs other than the license badge, code identifiers and file names stay exactly as they are.
- Inside ```bash blocks only translate placeholders in angle brackets (for example <tu-proyecto> becomes <your-project>); commands stay byte-for-byte identical.
- Translate image alt text, link text, table text, the text inside ```text blocks, and comments in the project tree.
- Table-of-contents links must point to the GitHub anchors of the English headings (lowercase, spaces as hyphens, punctuation removed).
- The language switcher line becomes: [Español](README.md) · **English**
- In the license badge, use the label "License: MIT" and the shields.io text "license-MIT".
- The app's interface and error messages are in Spanish; where the Spanish README says errors are shown in Spanish, keep that meaning.
- Keep Pokémon names, product names (Firebase, Jetpack Compose, PokéAPI) and the author's name unchanged.

You also receive the current English README. Where a Spanish passage has not changed in meaning, reuse its existing English \
translation verbatim, so the diff only shows real changes. Where the English file has drifted from the Spanish source, \
the Spanish source wins.

Return the complete English README between <readme> and </readme> tags, with nothing else outside the tags."""


def build_prompt(spanish: str, english: str) -> str:
    return (
        "<spanish_readme>\n" + spanish + "\n</spanish_readme>\n\n"
        "<current_english_readme>\n" + (english or "(no existe todavía)") + "\n</current_english_readme>"
    )


def extract_readme(text: str) -> str:
    match = re.search(r"<readme>\s*(.*?)\s*</readme>", text, flags=re.S)
    if not match:
        raise ValueError("La respuesta no contiene las etiquetas <readme>.")
    return match.group(1).rstrip() + "\n"


def translate(spanish: str, english: str) -> str:
    client = anthropic.Anthropic()
    with client.beta.messages.stream(
        model=MODEL,
        max_tokens=64000,
        system=SYSTEM_PROMPT,
        output_config={"effort": "high"},
        # Si el modelo rechazara la petición, la API la reintenta con el modelo de respaldo por defecto.
        betas=["server-side-fallback-2026-07-01"],
        fallbacks="default",
        messages=[{"role": "user", "content": build_prompt(spanish, english)}],
    ) as stream:
        message = stream.get_final_message()

    print(f"Modelo: {message.model} · request id: {message._request_id} · "
          f"tokens entrada/salida: {message.usage.input_tokens}/{message.usage.output_tokens}")

    if message.stop_reason == "refusal":
        category = message.stop_details.category if message.stop_details else None
        raise RuntimeError(f"El modelo rechazó la petición (categoría: {category}).")
    if message.stop_reason == "max_tokens":
        raise RuntimeError("La traducción se cortó por max_tokens.")

    text = "".join(block.text for block in message.content if block.type == "text")
    return extract_readme(text)


def main() -> int:
    spanish = ES.read_text(encoding="utf-8")
    english = EN.read_text(encoding="utf-8") if EN.exists() else ""
    try:
        translated = translate(spanish, english)
    except anthropic.AuthenticationError:
        print("::error::ANTHROPIC_API_KEY no es válida.")
        return 1
    except anthropic.RateLimitError:
        print("::error::Límite de peticiones de la API alcanzado; vuelve a lanzar el workflow más tarde.")
        return 1
    except anthropic.APIStatusError as e:
        print(f"::error::Error de la API ({e.status_code}): {e.message}")
        return 1
    except anthropic.APIConnectionError:
        print("::error::No se pudo conectar con la API de Anthropic.")
        return 1
    except (RuntimeError, ValueError) as e:
        print(f"::error::{e}")
        return 1

    if translated == english:
        print("La traducción no cambia README.en.md.")
    else:
        EN.write_text(translated, encoding="utf-8")
        print("README.en.md actualizado.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
