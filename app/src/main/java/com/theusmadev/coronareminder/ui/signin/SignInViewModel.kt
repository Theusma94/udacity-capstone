package com.theusmadev.coronareminder.ui.signin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel: ViewModel() {

    val emailContent = MutableLiveData<String>()
    val passwordContent = MutableLiveData<String>()

    fun checkFields() {
        Log.d("Teste","email: ${emailContent.value} / password: ${passwordContent.value}")
    }
}