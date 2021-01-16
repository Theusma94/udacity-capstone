package com.theusmadev.coronareminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theusmadev.coronareminder.data.local.model.CoronaData

@Database(entities = [CoronaData::class], version = 1)
abstract class CoronaDatabase: RoomDatabase() {
    abstract val coronaDao: CoronaDao
}