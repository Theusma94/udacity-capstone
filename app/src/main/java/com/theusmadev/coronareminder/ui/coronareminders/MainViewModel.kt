package com.theusmadev.coronareminder.ui.coronareminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theusmadev.coronareminder.data.local.prefs.PreferencesHelper
import com.theusmadev.coronareminder.data.repository.CoronaRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val coronaRepository: CoronaRepository,
    private val preferencesHelper: PreferencesHelper): ViewModel() {

    fun clearData() {
        viewModelScope.launch {
            coronaRepository.clearStateDatabase()
            coronaRepository.clearCountryDatabase()
            preferencesHelper.clearCountryChoosed()
        }
    }
}