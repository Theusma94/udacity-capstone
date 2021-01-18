package com.theusmadev.coronareminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theusmadev.coronareminder.data.local.model.CoronaData
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem

@Database(entities = [CoronaData::class, ReminderDataItem::class], version = 1)
abstract class CoronaDatabase: RoomDatabase() {
    abstract val coronaDao: CoronaDao
    abstract val remindersDao: RemindersDao
}