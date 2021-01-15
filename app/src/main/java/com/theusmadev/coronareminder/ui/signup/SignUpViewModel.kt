package com.theusmadev.coronareminder.ui.signup

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.theusmadev.coronareminder.data.User

class SignUpViewModel: ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userCreated = MutableLiveData<Boolean?>()
    val userCreated: LiveData<Boolean?> = _userCreated

    val emailRegister = MutableLiveData<String>()
    val passwordRegister = MutableLiveData<String>()

    fun checkToCreate() {
        if(!emailRegister.value.isNullOrEmpty() && !passwordRegister.value.isNullOrEmpty()) {
            registerUser(User(
                emailRegister.value!!,
                passwordRegister.value!!
            ))
        }
    }

    private fun registerUser(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userCreated.postValue(true)
                } else {
                    _userCreated.postValue(false)
                }
            }
    }

}