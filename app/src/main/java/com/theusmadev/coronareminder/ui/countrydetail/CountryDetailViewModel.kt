package com.theusmadev.coronareminder.ui.countrydetail

import androidx.lifecycle.*
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import com.theusmadev.coronareminder.utils.ResponseState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CountryDetailViewModel(
        private val preferencesHelper: PreferencesHelper,
        private val coronaRepository: CoronaRepository): ViewModel() {

    val statesList: LiveData<List<CoronaStateData>> = coronaRepository.getStates().asLiveData()

    val statusLoading = MutableLiveData<Boolean>(false)

    private val _dataNotFound = MutableLiveData<Boolean>(false)
    val dataNotFound: LiveData<Boolean> = _dataNotFound

    var countryChoosed = ""
    var job: Job? = null

    fun startJob() {
        job?.start()
    }

    fun cancelJob() {
        job?.cancel()
    }

    fun getCountryCoronaInfo() {
        statusLoading.value = true
        _dataNotFound.value = false
        countryChoosed = preferencesHelper.getCountryChoosed()
        if(countryChoosed.isNotEmpty()) {
            job = viewModelScope.launch {
                coronaRepository.getStates().collect { states ->
                    if(states.isEmpty()) {
                        coronaRepository.refreshStates(countryChoosed).collect { response ->
                            when(response) {
                                ResponseState.Loading -> { }
                                is ResponseState.Error -> statusLoading.postValue(false)
                                is ResponseState.Success -> {
                                    if(response.item.isEmpty()) {
                                        _dataNotFound.value = true
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