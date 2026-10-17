package com.mindflip.memorygame

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.webkit.JavascriptInterface
import android.widget.Toast

class WebAppInterface(private val context: Context, private val activity: MainActivity) {

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun triggerVibrate(milliseconds: Long) {
        val duration = milliseconds.coerceIn(0L, 1000L)
        if (duration == 0L) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    @JavascriptInterface
    fun showAd(type: String) {
        activity.runOnUiThread {
            activity.showNativeAd(type)
        }
    }

    @JavascriptInterface
    fun launchBillingFlow() {
        activity.runOnUiThread {
            activity.purchasePremium()
        }
    }

    @JavascriptInterface
    fun restorePurchases() {
        activity.runOnUiThread {
            activity.restorePurchases()
        }
    }
}
