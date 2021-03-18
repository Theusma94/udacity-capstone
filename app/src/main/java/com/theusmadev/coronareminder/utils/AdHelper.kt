package com.theusmadev.coronareminder.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.theusmadev.coronareminder.BuildConfig

class AdHelper(val context: Context) {

    @Volatile
    private var interstitialAd: InterstitialAd? = null
    private var listener: AdCallback? = null

    init {
        MobileAds.initialize(context)
    }
    fun setListener(listener: AdCallback) {
        this.listener = listener
    }

    fun initInterstitialAd() {
        if(interstitialAd == null) {
            InterstitialAd.load(context,BuildConfig.AD_INTER_ID,
                AdRequest.Builder().build(), object:
                    InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        interstitialAd = null
                        listener?.interstitialFinalized(false)
                    }
                    override fun onAdLoaded(p0: InterstitialAd) {
                        interstitialAd = p0
                        listener?.interstitialFinalized(true)
                    }
                })
        }
    }

    fun showInterstitialAd(activity: Activity) {
        interstitialAd?.show(activity)
    }
}

interface AdCallback {
    fun interstitialFinalized(isCreated: Boolean)
}