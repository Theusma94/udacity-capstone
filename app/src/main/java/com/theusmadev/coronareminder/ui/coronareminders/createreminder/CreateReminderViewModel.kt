package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.data.receiver.AlarmReceiver
import com.theusmadev.coronareminder.data.repository.ReminderRepository
import com.theusmadev.coronareminder.utils.CalendarUtils
import com.theusmadev.coronareminder.utils.getUniqueId
import kotlinx.coroutines.launch
import java.util.*

class CreateReminderViewModel (
        val app: Application,
        val reminderRepository: ReminderRepository,
        private val calendarUtils: CalendarUtils): AndroidViewModel(app) {

    val titleReminder = MutableLiveData<String>()
    val dateAndHourReminder = MutableLiveData<String>()

    private var notifyPendingIntent: PendingIntent? = null
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var notifyIntent: Intent? = null

    private val _startSaveGeofence = MutableLiveData<ReminderDataItem>()
    val startSaveGeofence: LiveData<ReminderDataItem> = _startSaveGeofence

    private val _showDateSelected = MutableLiveData<Boolean?>()
    val showDateSelected: LiveData<Boolean?> = _showDateSelected

    fun checkFields() {
        if(!titleReminder.value.isNullOrEmpty() && !dateAndHourReminder.value.isNullOrEmpty()) {
            val timestamp = calendarUtils.getTimestamp(dateAndHourReminder.value!!)
            val reminderDataItem = ReminderDataItem(
                    requestCode = getUniqueId(),
                    title = titleReminder.value,
                    dateTimestamp = timestamp
            )
            startAlarm(reminderDataItem)
        }
    }

    private fun startAlarm(reminderDataItem: ReminderDataItem) {
        Log.d("Teste","startAlarm()")
        setupIntents(reminderDataItem)
        val calendar: Calendar = calendarUtils.getCalendar()
        Log.d("Teste","day ${calendar.get(Calendar.DAY_OF_MONTH)}")
        Log.d("Teste","hour ${calendar.get(Calendar.HOUR_OF_DAY)}")
        Log.d("Teste","minute ${calendar.get(Calendar.MINUTE)}")
        val trigger = calendar.timeInMillis
        notifyPendingIntent?.let {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    trigger,
                    it
            )
        }
        viewModelScope.launch {
            reminderRepository.saveReminder(reminderDataItem)
        }
    }

    private fun setupIntents(reminderDataItem: ReminderDataItem) {
        notifyIntent = Intent(app, AlarmReceiver::class.java).apply {
            putExtra("data", reminderDataItem.title)
            putExtra("timestamp",reminderDataItem.dateTimestamp)
        }
        notifyPendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                getUniqueId(),
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

    }
}