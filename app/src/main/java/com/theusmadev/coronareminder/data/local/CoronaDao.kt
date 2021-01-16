package com.theusmadev.coronareminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theusmadev.coronareminder.data.local.model.CoronaData
import kotlinx.coroutines.flow.Flow

@Dao
interface CoronaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountries(items: List<CoronaData>)

    @Query("SELECT * FROM corona_stats WHERE region == 'Global'")
    fun getGlobalStats(): Flow<CoronaData>

    @Query("SELECT * FROM corona_stats WHERE :countryName == region")
    fun getSelectedCountry(countryName: String): Flow<CoronaData>

    @Query("SELECT region FROM corona_stats WHERE region != 'Global'")
    fun getListOfCountries(): Flow<List<String>>

}