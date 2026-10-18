package com.mindflip.memorygame

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Fullscreen Immersive Mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        webView = WebView(this)
        setContentView(webView)

        // Initialize Firebase & Mobile Ads SDK
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        MobileAds.initialize(this) {}

        loadInterstitialAd()
        loadRewardedAd()

        // WebView Settings
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = false
            allowFileAccess = false
            allowContentAccess = false
            cacheMode = WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                safeBrowsingEnabled = true
            }
        }

        // Add JavaScript Interface Bridge
        webView.addJavascriptInterface(WebAppInterface(this, this), "AndroidNative")

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return true
                val isTrustedLocalAsset = uri.scheme == "https" &&
                    uri.host == "appassets.androidplatform.net" &&
                    uri.path.orEmpty().startsWith("/assets/www/")

                if (isTrustedLocalAsset) return false

                if (uri.scheme == "https" || uri.scheme == "http") {
                    return try {
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                        true
                    } catch (_: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "No app is available to open this link.",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                }

                // Never let arbitrary schemes navigate the WebView that exposes the bridge.
                return true
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Load Local HTML / Dist Assets
        if (savedInstanceState == null) {
            webView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
        } else {
            webView.restoreState(savedInstanceState)
        }

        // Handle Hardware Back Button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            getString(R.string.rewarded_ad_unit_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            }
        )
    }

    fun showNativeAd(type: String) {
        when (type) {
            "interstitial" -> {
                if (interstitialAd != null) {

                    interstitialAd?.fullScreenContentCallback =
                        object : com.google.android.gms.ads.FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null

                                webView.evaluateJavascript(
                                    "window.onAndroidInterstitialClosed && window.onAndroidInterstitialClosed();",
                                    null
                                )

                                loadInterstitialAd()
                            }

                            override fun onAdShowedFullScreenContent() {
                                interstitialAd = null
                            }
                            override fun onAdFailedToShowFullScreenContent(
                                adError: com.google.android.gms.ads.AdError
                            ) {
                                interstitialAd = null
                                loadInterstitialAd()
                            }
                        }

                    interstitialAd?.show(this)

                } else {
                    Toast.makeText(this, "Ad loading...", Toast.LENGTH_SHORT).show()
                }
            }
            "rewarded" -> {
                if (rewardedAd != null) {
                    rewardedAd?.show(this) {
                        webView.evaluateJavascript("window.onAndroidRewardedClaimed && window.onAndroidRewardedClaimed();", null)
                    }
                    loadRewardedAd()
                } else {
                    Toast.makeText(this, "Rewarded Ad loading...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun purchasePremium() {
        // Never grant Premium until a real Play Billing product and verification flow exist.
        Toast.makeText(
            this,
            "Premium purchases are not configured yet. No purchase was made.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun restorePurchases() {
        // Restoration must query verified Google Play purchases; never grant Premium locally.
        Toast.makeText(
            this,
            "Purchase restoration is not configured yet.",
            Toast.LENGTH_LONG
        ).show()
    }
}
