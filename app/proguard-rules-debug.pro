# This is a configuration file for ProGuard.
# https://www.guardsquare.com/en/products/proguard/manual/usage

# Print final merged ProGuard configurations in the following file.
-printconfiguration "build/outputs/mapping/configurationDebug.txt"

# Disable warnings, obfuscation and optimizations in debug builds.
-ignorewarnings
-dontobfuscate
-dontoptimize

# Retain some debug information about local variables (only needed line in debug build type).
-keepattributes LocalVariableTable, LocalVariableTypeTable

# *** LEAKCANARY ***
-keepclassmembers class org.eclipse.mat.** { *; }
-keepclassmembers class com.squareup.leakcanary.** { *; }

# *** MOSHI ***
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

# Enum field names are used by the integrated EnumJsonAdapter.
# Annotate enums with @JsonClass(generateAdapter = false) to use them with Moshi.
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
}

# The name of @JsonClass types is used to look up the generated adapter.
-keepnames @com.squareup.moshi.JsonClass class *

# Retain generated JsonAdapters if annotated type is retained.
-if @com.squareup.moshi.JsonClass class *
-keep class <1>JsonAdapter {
    <init>(...);
    <fields>;
}

-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
-keepnames @kotlin.Metadata class com.nordicoslink.energycostcalculator.data.settings.model.**
-keep class com.nordicoslink.energycostcalculator.data.settings.model.** { *; }
-keepclassmembers class com.nordicoslink.energycostcalculator.data.settings.model.** { *; }