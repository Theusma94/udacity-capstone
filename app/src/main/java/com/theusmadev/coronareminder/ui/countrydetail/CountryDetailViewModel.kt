package com.theusmadev.coronareminder.ui.countrydetail

import android.util.Log
import androidx.lifecycle.*
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import com.theusmadev.coronareminder.utils.ResponseState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CountryDetailViewModel(
        private val preferencesHelper: PreferencesHelper,
        private val coronaRepository: CoronaRepository): ViewModel() {

    private val _statesList = MutableLiveData<List<CoronaStateData>>()
    val statesList: LiveData<List<CoronaStateData>> = coronaRepository.getStates().asLiveData()

    val statusLoading = MutableLiveData<Boolean>(false)
    val dataNotFound = MutableLiveData<Boolean>(false)
    var countryChoosed = ""

    fun getCountryCoronaInfo() {
        Log.d("Teste","getCountryCoronaInfo")
        statusLoading.value = true
        dataNotFound.value = false
        countryChoosed = preferencesHelper.getCountryChoosed()
        if(countryChoosed.isNotEmpty()) {
            viewModelScope.launch {
                coronaRepository.getStates().collect { states ->
                    if(states.isEmpty()) {
                        coronaRepository.refreshStates(countryChoosed).collect { response ->
                            when(response) {
                                ResponseState.Loading -> { }
                                is ResponseState.Error -> statusLoading.postValue(false)
                                is ResponseState.Success -> {
                                    if(response.item.isEmpty()) {
                                        dataNotFound.value = true
                                    }
                                    statusLoading.postValue(false)
                                }
                            }
                        }
                    } else {
                        statusLoading.postValue(false)
                    }
                }

            }
        }
    }

    fun retry() {
        getCountryCoronaInfo()
    }

}