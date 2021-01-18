package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.data.repository.ReminderRepository
import kotlinx.coroutines.launch

class CreateReminderViewModel (val reminderRepository: ReminderRepository): ViewModel() {

    val titleReminder = MutableLiveData<String>()
    val descriptionReminder = MutableLiveData<String>()
    val selectedLocation = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()

    var isFavorite = false
    var reminderDataItem: ReminderDataItem? = null

    private val _startSaveGeofence = MutableLiveData<ReminderDataItem>()
    val startSaveGeofence: LiveData<ReminderDataItem> = _startSaveGeofence

    fun checkFields() {
        if(titleReminder.value.isNullOrEmpty() ||
            descriptionReminder.value.isNullOrEmpty() ||
            selectedLocation.value.isNullOrEmpty() ||
            selectedPOI.value == null) {
            //Erro
        } else {
            reminderDataItem = ReminderDataItem(
                titleReminder.value,
                descriptionReminder.value,
                selectedLocation.value,
                selectedPOI.value!!.latLng.latitude,
                selectedPOI.value!!.latLng.longitude,
                isFavorite
            )
            if(isFavorite) {
                viewModelScope.launch {
                    reminderRepository.saveFavoriteReminder(reminderDataItem!!)
                }
            }
            _startSaveGeofence.value = reminderDataItem
        }
    }

    fun checkFavoriteStatus(isChecked: Boolean) {
        isFavorite = isChecked
    }
}