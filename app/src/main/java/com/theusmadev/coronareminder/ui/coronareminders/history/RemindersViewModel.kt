package com.theusmadev.coronareminder.ui.coronareminders.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemindersViewModel(
        private val reminderRepository: ReminderRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    val statusLoading = MutableLiveData<Boolean>()

    private val _listOfReminders = MutableLiveData<List<ReminderDataItem>>()
    val listOfReminders: LiveData<List<ReminderDataItem>> = _listOfReminders

    fun getRemindersFromDb() {
        statusLoading.value = true
        viewModelScope.launch {
            withContext(dispatcher) {
                reminderRepository.allReminders.collect {
                    statusLoading.postValue(false)
                    _listOfReminders.postValue(it)
                }
            }
        }
    }
}