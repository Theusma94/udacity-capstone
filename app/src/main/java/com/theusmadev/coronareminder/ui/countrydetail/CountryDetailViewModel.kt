package com.theusmadev.coronareminder.ui.countrydetail

import androidx.lifecycle.*
import com.theusmadev.coronareminder.data.local.model.CoronaCountryData
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CountryDetailViewModel(
        private val preferencesHelper: PreferencesHelper,
        private val coronaRepository: CoronaRepository): ViewModel() {

    private val coronaCountry = MutableLiveData<CoronaCountryData>()
    private val coronaState = MutableLiveData<CoronaStateData>()

    private val _statesList = MutableLiveData<List<CoronaStateData>>()
    val statesList: LiveData<List<CoronaStateData>> = coronaRepository.getStates().asLiveData()

    var countryChoosed = ""

    fun getCountryCoronaInfo() {
        countryChoosed = preferencesHelper.getCountryChoosed()
        if(countryChoosed.isNotEmpty()) {
            viewModelScope.launch {
                coronaRepository.getStates().collect { states ->
                    if(states.isEmpty()) {
                        coronaRepository.getSelectedCountry(countryChoosed).collect {
                            coronaCountry.postValue(it)
                            coronaRepository.refreshStates(countryChoosed).collect()
                        }
                    } else {
//                        _statesList.postValue(states)
                    }
                }

            }
        } else {
            // Não escolheu o país ainda
        }
    }

}