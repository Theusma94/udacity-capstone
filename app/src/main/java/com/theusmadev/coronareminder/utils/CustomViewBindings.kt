package com.theusmadev.coronareminder.utils

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.theusmadev.coronareminder.R
import org.w3c.dom.Text
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
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .fit()
            .centerCrop()
            .into(imageView);
    }

    private fun processCountry(countryName: String): String {
        val countryTransformed = countryName.toLowerCase().replace(" ","-")
        return "https://www.countryflags.com/wp-content/uploads/$countryTransformed-flag-png-large.png"
    }

    @BindingAdapter(value = ["loading","bg_color_loading"])
    @JvmStatic
    fun TextView.applyLoading(loading: Boolean, color: String) {
        if(loading) {
            this.setBackgroundColor(Color.parseColor(color))
        } else {
            this.setBackgroundColor(ContextCompat.getColor(this.context, android.R.color.transparent))
        }
    }
}