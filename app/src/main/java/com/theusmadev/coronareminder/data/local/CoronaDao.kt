package com.theusmadev.coronareminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import kotlinx.coroutines.flow.Flow

@Dao
interface CoronaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountries(items: List<CoronaCountryData>)

    @Query("SELECT * FROM corona_country WHERE region == 'Global'")
    fun getGlobalStats(): Flow<CoronaCountryData>

    @Query("SELECT * FROM corona_country WHERE :countryName == region")
    fun getSelectedCountry(countryName: String): Flow<CoronaCountryData>

    @Query("SELECT region FROM corona_country WHERE region != 'Global'")
    fun getListOfCountries(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStates(items: List<CoronaStateData>)

    @Query("SELECT * FROM corona_states")
    fun getListOfStates(): Flow<List<CoronaStateData>>

}