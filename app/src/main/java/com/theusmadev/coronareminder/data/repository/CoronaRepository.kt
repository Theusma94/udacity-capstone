package com.theusmadev.coronareminder.data.repository

import com.theusmadev.coronareminder.data.local.CoronaDatabase
import com.theusmadev.coronareminder.data.local.model.CoronaData
import com.theusmadev.coronareminder.data.network.CoronaApiService
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
    val countries = coronaDatabase.coronaDao.getListOfCountries()

    fun getSelectedCountry(selected: String): Flow<CoronaData> = coronaDatabase.coronaDao.getSelectedCountry(selected)

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

    private fun processResponse(body: Map<String, CountryStats>?): List<CoronaData> {
        val dataToSave = mutableListOf<CoronaData>()
        if (body != null) {
            for((key,value) in body) {
                dataToSave.add(
                    CoronaData(
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
}