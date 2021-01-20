package com.theusmadev.coronareminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem

@Database(entities = [CoronaCountryData::class, ReminderDataItem::class, CoronaStateData::class], version = 1)
abstract class CoronaDatabase: RoomDatabase() {
    abstract val coronaDao: CoronaDao
    abstract val remindersDao: RemindersDao
}