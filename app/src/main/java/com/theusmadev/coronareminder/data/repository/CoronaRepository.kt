package com.theusmadev.coronareminder.data.repository

import com.theusmadev.coronareminder.data.local.CoronaDatabase
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.data.network.CoronaApiService
import com.theusmadev.coronareminder.data.network.CoronaStats
import com.theusmadev.coronareminder.data.network.CountryStats
import com.theusmadev.coronareminder.utils.ResponseState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CoronaRepository(
        private val coronaApiService: CoronaApiService,
        private val coronaDatabase: CoronaDatabase) {

    val globalCorona = coronaDatabase.coronaDao.getGlobalStats()

    fun getStates(): Flow<List<CoronaStateData>> = coronaDatabase.coronaDao.getListOfStates()

    suspend fun getSelectedCountry(selected: String, dispatcher: CoroutineDispatcher = Dispatchers.IO): CoronaCountryData {
        return withContext(dispatcher) {
            coronaDatabase.coronaDao.getSelectedCountry(selected)
        }
    }

    suspend fun getListOfCountries(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<String> {
        return withContext(dispatcher) {
            coronaDatabase.coronaDao.getListOfCountries()
        }
    }

    @ExperimentalCoroutinesApi
    fun refreshCountries(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<ResponseState<List<String>>> {
        return flow {
            emit(ResponseState.Loading)
            withContext(dispatcher) {
                emit(fetchCountries())
            }
        }.flowOn(dispatcher)
    }

    private fun fetchCountries(): ResponseState<List<String>> {
        return try {
            val response = coronaApiService.getCountries("cases").execute()
            val countryList = response.body()?.keys?.toList() ?: listOf()
            val processed = processResponse(response.body())
            coronaDatabase.coronaDao.insertCountries(processed)
            ResponseState.Success(countryList)
        } catch (throwable: Throwable) {
            ResponseState.Error(throwable)
        }
    }

    private fun processResponse(body: Map<String, CountryStats>?): List<CoronaCountryData> {
        val dataToSave = mutableListOf<CoronaCountryData>()
        if (body != null) {
            for((key,value) in body) {
                dataToSave.add(
                        CoronaCountryData(
                                region = key,
                                confirmed = value.data.confirmed,
                                recovered = value.data.recovered,
                                deaths = value.data.deaths
                        )
                )
            }
        }
        return dataToSave
    }

    fun refreshStates(countryName: String, dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<ResponseState<List<CoronaStateData>>> {
        return flow {
            emit(ResponseState.Loading)
            withContext(dispatcher) {
                emit(fetchStates(countryName))
            }
        }.flowOn(dispatcher)
    }

    private fun fetchStates(countryName: String): ResponseState<List<CoronaStateData>> {
        return try {
            val response = coronaApiService.getStates("cases?country=$countryName").execute()
            val processed = processStatesResponse(response.body())
            coronaDatabase.coronaDao.insertStates(processed)
            ResponseState.Success(processed)
        } catch (throwable: Throwable) {
            ResponseState.Error(throwable)
        }
    }

    private fun processStatesResponse(body: Map<String, CoronaStats>?): List<CoronaStateData> {
        val dataToSave = mutableListOf<CoronaStateData>()
        if (body != null) {
            for((key,value) in body) {
                if(key != "All") {
                    dataToSave.add(
                            CoronaStateData(
                                    region = key,
                                    confirmed = value.confirmed,
                                    recovered = value.recovered,
                                    deaths = value.deaths
                            )
                    )
                }
            }
        }
        return dataToSave
    }

    suspend fun clearStateDatabase(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withContext(dispatcher) {
            coronaDatabase.coronaDao.deleteStates()
        }
    }

    suspend fun clearCountryDatabase(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withContext(dispatcher) {
            coronaDatabase.coronaDao.deleteCountries()
        }
    }
}