package com.theusmadev.coronareminder.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.theusmadev.coronareminder.data.User

class SignUpViewModel: ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userCreated = MutableLiveData<Boolean?>()
    val userCreated: LiveData<Boolean?> = _userCreated

    private val _showLoading = MutableLiveData<Boolean?>()
    val showLoading: LiveData<Boolean?> = _showLoading

    val emailRegister = MutableLiveData<String>()
    val passwordRegister = MutableLiveData<String>()

    fun checkToCreate() {
        _showLoading.value = true
        if(!emailRegister.value.isNullOrEmpty() && !passwordRegister.value.isNullOrEmpty()) {
            registerUser(User(
                emailRegister.value!!,
                passwordRegister.value!!
            ))
        } else {
            _showLoading.value = false
            _userCreated.value = false
        }
    }

    private fun registerUser(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                _showLoading.value = false
                if (task.isSuccessful) {
                    _userCreated.postValue(true)
                } else {
                    _userCreated.postValue(false)
                }
            }
    }

    fun onClearData() {
        _showLoading.value = null
        _userCreated.value = null
    }

}