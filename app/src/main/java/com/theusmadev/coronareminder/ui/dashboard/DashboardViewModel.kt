package com.theusmadev.coronareminder.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import com.theusmadev.coronareminder.utils.ResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DashboardViewModel (
        val preferencesHelper: PreferencesHelper,
        val coronaRepository: CoronaRepository): ViewModel() {

    val coronaGlobal = MutableLiveData<CoronaCountryData>()
    val coronaCountry = MutableLiveData<CoronaCountryData>()

    private val _showListCountries = MutableLiveData<List<String>>()
    val showListCountries: LiveData<List<String>> = _showListCountries

    val isLoading = MutableLiveData<Boolean>()

    @ExperimentalCoroutinesApi
    fun getCoronaInfo() {
        isLoading.value = true
        viewModelScope.launch {
            coronaRepository.globalCorona.collect {
                if(it == null) {
                    coronaRepository.refreshCountries().collect {
                        when(it) {
                            ResponseState.Loading -> { }
                            is ResponseState.Error -> { }
                            is ResponseState.Success -> { }
                        }
                    }
                } else {
                    isLoading.value = false
                    coronaGlobal.postValue(it)
                    checkCountrySelected()
                }
            }
        }
    }

    fun clearSelectedCountry() {
        preferencesHelper.setCountryChoosed("")
        checkCountrySelected()
    }

    private fun checkCountrySelected() {
        viewModelScope.launch {
            val countryChoosed = preferencesHelper.getCountryChoosed()
            if (countryChoosed.isEmpty()) {
                val countries = coronaRepository.getListOfCountries().insertOnTop("Choose one country")
                _showListCountries.postValue(countries)
                Log.d("Teste", countries.toString())
            }
            else {
                getCountryCoronaInfo(countryChoosed)
            }
        }
    }

    fun getCountryCoronaInfo(countryChoosed: String) {
        viewModelScope.launch {
            val countryData = coronaRepository.getSelectedCountry(countryChoosed)
            preferencesHelper.setCountryChoosed(countryChoosed)
            coronaCountry.postValue(countryData)
        }
    }

    @ExperimentalCoroutinesApi
    fun updateData() {
        isLoading.value = true
        viewModelScope.launch {
            coronaRepository.refreshCountries().collect {
                when(it) {
                    ResponseState.Loading -> { }
                    is ResponseState.Error -> {
                        isLoading.value = false
                    }
                    is ResponseState.Success -> {
                        isLoading.value = false
                    }
                }
            }
        }
    }

}

fun <T> List<T>.insertOnTop(item: T): List<T> {
    val newList = mutableListOf<T>()
    newList.add(item)
    newList.addAll(this)
    return newList
}