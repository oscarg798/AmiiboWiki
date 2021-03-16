-keep class com.oscarg798.amiibowiki.core.persistence.* { *; }
-keep class com.oscarg798.amiibowiki.core.network.models.* { *; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
