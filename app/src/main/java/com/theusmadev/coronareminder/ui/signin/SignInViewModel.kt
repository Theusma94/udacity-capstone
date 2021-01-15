package com.theusmadev.coronareminder.ui.signin

import android.R.attr.password
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.theusmadev.coronareminder.data.User


class SignInViewModel: ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private val _userLogged = MutableLiveData<Boolean?>()
    val userLogged: LiveData<Boolean?> = _userLogged

    val emailContent = MutableLiveData<String>()
    val passwordContent = MutableLiveData<String>()

    fun checkFields() {
        if(!emailContent.value.isNullOrEmpty() && !passwordContent.value.isNullOrEmpty()) {
            signinUser(
                User(
                    emailContent.value!!,
                    passwordContent.value!!
                )
            )
        }
    }

    private fun signinUser(user: User) {
        firebaseAuth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userLogged.postValue(true)
                } else {
                    _userLogged.postValue(false)
                }
            }
    }
}