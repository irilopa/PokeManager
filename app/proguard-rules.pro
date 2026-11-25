# Reglas de R8 para la versión de publicación.

# Firestore (toObject/set) y Gson leen y escriben los campos por reflexión:
# hay que conservar los modelos tal cual, con sus constructores sin argumentos.
-keep class org.ivan.pokmanager.domain.model.** { *; }
-keep class org.ivan.pokmanager.data.model.** { *; }

# Genéricos (List<...> en los DTO de Gson) y anotaciones como @DocumentId.
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Crashlytics: trazas con números de línea legibles tras desofuscar.
-keepattributes SourceFile, LineNumberTable
-keep public class * extends java.lang.Exception
