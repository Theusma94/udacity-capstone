package com.theusmadev.coronareminder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corona_stats")
data class CoronaData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val region: String,
    val confirmed: Long,
    val recovered: Long,
    val deaths: Long
)