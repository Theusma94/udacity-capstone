package com.theusmadev.coronareminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RemindersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReminder(reminder: ReminderDataItem)

    @Query("SELECT * FROM reminders")
    fun getReminders(): Flow<List<ReminderDataItem>>
}