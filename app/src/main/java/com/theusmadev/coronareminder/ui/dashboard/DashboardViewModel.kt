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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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


    @ExperimentalCoroutinesApi
    fun getCoronaInfo() {
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
                    coronaGlobal.postValue(it)
                }
            }
        }
        val countryChoosed = preferencesHelper.getCountryChoosed()
        if(countryChoosed.isNullOrEmpty()) {
            viewModelScope.launch {
               coronaRepository.countries.map {
                   it.dropLast(1).insertOnTop("Choose one country")
               }.collect {
                   _showListCountries.postValue(it)
                   Log.d("Teste",it.toString())
               }
            }
        } else {
            getCountryCoronaInfo(countryChoosed)
        }
    }

    fun getCountryCoronaInfo(countryChoosed: String) {
        viewModelScope.launch {
            coronaRepository.getSelectedCountry(countryChoosed).collect {
                preferencesHelper.setCountryChoosed(countryChoosed)
                coronaCountry.postValue(it)
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