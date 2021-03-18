package com.theusmadev.coronareminder.data.local.prefs

import android.content.Context
import androidx.preference.PreferenceManager

class PreferencesHelper(private val context: Context) {

    private val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)

    fun setCountryChoosed(country: String) {
        sharedPreferences.edit().putString(KEY_COUNTRY, country).apply()
    }

    fun getCountryChoosed(): String {
        return sharedPreferences.getString(KEY_COUNTRY,"")!!
    }

    fun clearCountryChoosed() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val KEY_COUNTRY = "country_choosed"
    }
}