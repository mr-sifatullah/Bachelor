# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# removes such information by default, so configure it to keep all of it.

-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

##---------------End: proguard configuration for Retrofit  ----------

##---------------Begin: proguard configuration for Places  ----------
-dontwarn com.google.android.libraries.places.**

##---------------Begin: proguard configuration for OkHttp  ----------
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn com.android.tools.profiler.agent.okhttp.OkHttp2Interceptor

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
# Prevent R8 from leaving ProfileData object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Firebase Firestore model annotations
-keepclassmembers class * {
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.IgnoreExtraProperties <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.ServerTimestamp <methods>;
}

# Prevent Firebase classes from being stripped
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firebase Messaging classes
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

# Retrofit and GSON
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep your custom notification-related data classes
-keep class com.sifat.bachelor.api.model.NotificationData { *; }
-keep class com.sifat.bachelor.api.model.Message { *; }
-keep class com.sifat.bachelor.api.model.Notifications { *; }
-keep class com.sifat.bachelor.api.ErrorResponse { *; }


# Keep your Firestore model classes
-keep class com.sifat.bachelor.chat.ChatMessage { *; }

# Keep your ViewModel
-keep class com.sifat.bachelor.home.HomeViewModel { *; }
-dontwarn com.sifat.bachelor.home.HomeViewModel

# Keep LiveData
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }



