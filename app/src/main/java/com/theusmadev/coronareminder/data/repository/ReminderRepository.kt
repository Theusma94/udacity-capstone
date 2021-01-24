package com.theusmadev.coronareminder.data.repository

import com.theusmadev.coronareminder.data.local.CoronaDatabase
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderRepository(val coronaDatabase: CoronaDatabase) {

    val allReminders = coronaDatabase.remindersDao.getReminders()

    suspend fun saveReminder(item: ReminderDataItem, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withContext(dispatcher) {
            coronaDatabase.remindersDao.saveReminder(item)
        }
    }
}