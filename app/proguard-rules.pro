# Keep JavaScript Interface methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Firebase models
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private "<fields>";
    !private "<methods>";
}

# Keep Google Mobile Ads (AdMob)
-keep class com.google.android.gms.ads.** { *; }

# Keep Google Play Billing
-keep class com.android.billingclient.api.** { *; }

# Keep Kotlin Coroutines & ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
