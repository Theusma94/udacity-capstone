package com.theusmadev.coronareminder.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.theusmadev.coronareminder.R
import java.lang.StringBuilder

object CustomViewBindings {

    @BindingAdapter(value = ["maskValue","appendText"])
    @JvmStatic
    fun applyMask(textView: TextView, value: String, appendText: String) {
        val stringBuilder = StringBuilder()
        val transformed = value.reversed().chunked(3).joinToString(".").reversed()
        stringBuilder.append(transformed).append(" ").append(appendText)
        textView.text = stringBuilder.toString()
    }

    @BindingAdapter("downloadFlag")
    @JvmStatic
    fun applyFlagImage(imageView: ImageView, countryName: String) {
        val url = processCountry(countryName)
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_google)
            .error(R.drawable.ic_death)
            .fit()
            .centerCrop()
            .into(imageView);
    }

    private fun processCountry(countryName: String): String {
        val countryTransformed = countryName.toLowerCase().replace(" ","-")
        return "https://www.countryflags.com/wp-content/uploads/$countryTransformed-flag-png-large.png"
    }
}