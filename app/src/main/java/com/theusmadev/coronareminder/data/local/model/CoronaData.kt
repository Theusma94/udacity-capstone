package com.theusmadev.coronareminder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

interface CoronaInfo {
    val region: String
    val confirmed: Long
    val recovered: Long
    val deaths: Long
}
@Entity(tableName = "corona_country")
data class CoronaCountryData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    override val region: String,
    override val confirmed: Long,
    override val recovered: Long,
    override val deaths: Long
): CoronaInfo

@Entity(tableName = "corona_states")
data class CoronaStateData(
        @PrimaryKey(autoGenerate = true) val id: Long = 0L,
        override val region: String,
        override val confirmed: Long,
        override val recovered: Long,
        override val deaths: Long
): CoronaInfo