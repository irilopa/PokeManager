#!/usr/bin/env python3
"""Comprueba que README.md (español) y README.en.md (inglés) siguen sincronizados.

Compara lo que no depende del idioma:
  - número de secciones (## y ###),
  - imágenes locales (en orden),
  - enlaces externos (sin contar insignias de shields.io),
  - comandos de los bloques ```bash (ignorando marcadores como <tu-proyecto>).

Con --base <ref>, además exige que si un README cambia respecto a esa referencia,
el otro también cambie.
"""
import argparse
import re
import subprocess
import sys
from pathlib import Path

ES, EN = Path("README.md"), Path("README.en.md")


def headings(text: str) -> list[str]:
    return [m.group(1) for m in re.finditer(r"^(#{2,3}) ", text, flags=re.M)]


def local_images(text: str) -> list[str]:
    return re.findall(r'<img src="(docs/[^"]+)"', text)


def external_links(text: str) -> set[str]:
    urls = re.findall(r"\((https?://[^)\s]+)\)", text)
    return {u for u in urls if "img.shields.io" not in u}


def bash_blocks(text: str) -> list[str]:
    blocks = re.findall(r"```bash\n(.*?)```", text, flags=re.S)
    return [re.sub(r"<[^>]+>", "<placeholder>", "\n".join(l.strip() for l in b.strip().splitlines())) for b in blocks]


def changed_files(base: str) -> set[str]:
    out = subprocess.run(["git", "diff", "--name-only", f"{base}...HEAD"], capture_output=True, text=True, check=True)
    return set(out.stdout.split())


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", help="referencia git con la que comparar (p. ej. origin/master)")
    args = parser.parse_args()

    errors = []
    for path in (ES, EN):
        if not path.exists():
            errors.append(f"Falta {path}")
    if errors:
        print("\n".join(errors))
        return 1

    es, en = ES.read_text(encoding="utf-8"), EN.read_text(encoding="utf-8")
    checks = [
        ("secciones (## / ###)", headings(es), headings(en)),
        ("imágenes locales", local_images(es), local_images(en)),
        ("enlaces externos", external_links(es), external_links(en)),
        ("comandos bash", bash_blocks(es), bash_blocks(en)),
    ]
    for name, a, b in checks:
        if a != b:
            errors.append(f"Distintos {name}:\n  README.md:    {a}\n  README.en.md: {b}")

    if args.base:
        changed = changed_files(args.base)
        es_changed, en_changed = str(ES) in changed, str(EN) in changed
        if es_changed != en_changed:
            only, other = (ES, EN) if es_changed else (EN, ES)
            errors.append(f"Se ha modificado {only} pero no {other}: actualiza las dos versiones en el mismo pull request.")

    if errors:
        print("README no sincronizado:\n\n" + "\n\n".join(errors))
        return 1
    print("README.md y README.en.md están sincronizados.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
