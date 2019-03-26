# This is a configuration file for ProGuard.
# https://www.guardsquare.com/en/products/proguard/manual/usage

# Print final merged ProGuard configurations in the following file.
-printconfiguration build/outputs/mapping/configuration.txt

# Disable optimizations that are known to cause problems.
-optimizations !code/allocation/variable,!method/removal/parameter

# Repackage all classes to save some space.
-repackageclasses ''

# *** KOTLIN ***
# Strip out Kotlin runtime null checks.
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
  static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# *** LEAKCANARY ***
-keepclassmembers class org.eclipse.mat.** { *; }
-keepclassmembers class com.squareup.leakcanary.** { *; }

# *** DAGGER ***
-dontwarn com.google.errorprone.annotations.**

# *** JSR 305 – ignore annotations for embedding nullability information. ***
-dontwarn javax.annotation.**

# *** OKIO ***
-dontwarn okio.**

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
#-if @com.squareup.moshi.JsonClass class *
-keep class *JsonAdapter {
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