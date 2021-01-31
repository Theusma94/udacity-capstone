package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        private val reminderRepository: ReminderRepository,
        private val calendarUtils: CalendarUtils): AndroidViewModel(app) {

    val titleReminder = MutableLiveData<String>()
    val dateAndHourReminder = MutableLiveData<String>()

    private var notifyPendingIntent: PendingIntent? = null
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var notifyIntent: Intent? = null

    private val _reminderCreated = MutableLiveData<Boolean?>()
    val reminderCreated: LiveData<Boolean?> = _reminderCreated

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
        setupIntents(reminderDataItem)
        val calendar: Calendar = calendarUtils.getCalendar()
        val trigger = calendar.timeInMillis
        notifyPendingIntent?.let {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    trigger,
                    it
            )
            viewModelScope.launch {
                reminderRepository.saveReminder(reminderDataItem)
            }
            _reminderCreated.value = true
        } ?: run {
            _reminderCreated.value = false
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

    fun onReminderCreated() {
        titleReminder.value = ""
        dateAndHourReminder.value = ""
        _reminderCreated.value = null
    }
}