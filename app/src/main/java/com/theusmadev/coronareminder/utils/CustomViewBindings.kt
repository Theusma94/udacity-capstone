package com.theusmadev.coronareminder.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
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
}