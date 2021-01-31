package com.theusmadev.coronareminder.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.theusmadev.coronareminder.R
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

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
    fun TextView.applyLoading(loading: Boolean, @ColorInt bgColor: Int) {
        if(loading) {
            this.setBackgroundColor(bgColor)
        } else {
            this.setBackgroundColor(ContextCompat.getColor(this.context, android.R.color.transparent))
        }
    }

    @BindingAdapter("date_alarm")
    @JvmStatic
    fun ImageView.chooseAlarmIcon(dateTimestamp: Long?) {
        val today = Date()
        if(today.time > dateTimestamp ?: 0L) {
            this.setImageResource(R.drawable.ic_alarm_off)
        } else {
            this.setImageResource(R.drawable.ic_alarm)
        }
    }

    @BindingAdapter("timestamp_to_date")
    @JvmStatic
    fun TextView.convertTimestamp(dateTimestamp: Long?) {
        val patternDefault = "dd/MM/yyyy HH:mm"
        val formatter = SimpleDateFormat(patternDefault, Locale.getDefault())
        dateTimestamp?.let {
            val date = Date(it)
            this.text = formatter.format(date)
        } ?: run {
            this.text = context.getString(R.string.error_parse)
        }

    }
}