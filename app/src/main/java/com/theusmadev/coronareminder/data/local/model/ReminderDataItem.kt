package com.theusmadev.coronareminder.data.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "reminders")
data class ReminderDataItem(
        @PrimaryKey(autoGenerate = false) var requestCode: Int,
        var title: String?,
        var dateTimestamp: Long?
)