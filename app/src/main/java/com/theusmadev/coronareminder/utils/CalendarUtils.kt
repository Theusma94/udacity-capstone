package com.theusmadev.coronareminder.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils: LifecycleObserver {

    private var isDateSeted: Boolean = false
    private var isHourSeted: Boolean = false
    private val calendar = Calendar.getInstance()
    private val patternDefault = "dd/MM/yyyy HH:mm"
    private val sdf = SimpleDateFormat(patternDefault, Locale.getDefault())
    var listener: CalendarListener? = null

    fun setUp(listener: CalendarListener) {
        this.listener = listener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        isDateSeted = false
        isHourSeted = false
        listener = null
    }

    @UiThread
    fun showPicker(context: Context) {
        TimePickerDialog(
                context,
                hourPicker,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show()
        val datePicker =  DatePickerDialog(
                context,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private var datePicker: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener {
        _, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        isDateSeted = true
        checkStatus()
    }


    private var hourPicker: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener {
        _, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        isHourSeted = true
        checkStatus()
    }

    private fun checkStatus() {
        if(isDateSeted && isHourSeted) {
            listener?.onDateAndHourSeted(sdf.format(calendar.time))
        }
    }

    fun getCalendar(): Calendar {
        return calendar
    }
}

interface CalendarListener {
    fun onDateAndHourSeted(dateAndHour: String)
}