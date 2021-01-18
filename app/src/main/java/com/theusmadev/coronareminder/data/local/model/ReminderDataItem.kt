package com.theusmadev.coronareminder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders")
data class ReminderDataItem(
        var title: String?,
        var description: String?,
        var location: String?,
        var latitude: Double?,
        var longitude: Double?,
        var isFavorite: Boolean?,
        @PrimaryKey(autoGenerate = false) val id: String = UUID.randomUUID().toString()
)