package com.theusmadev.coronareminder.ui.signin

import android.R.attr.password
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.theusmadev.coronareminder.data.User
import com.theusmadev.coronareminder.ui.coronareminders.CoronaRemindersActivity


class SignInViewModel: ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private val _userLogged = MutableLiveData<Boolean?>()
    val userLogged: LiveData<Boolean?> = _userLogged

    private val _showLoading = MutableLiveData<Boolean?>()
    val showLoading: LiveData<Boolean?> = _showLoading

    val emailContent = MutableLiveData<String>()
    val passwordContent = MutableLiveData<String>()

    fun checkFields() {
        _showLoading.value = true
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
                _showLoading.postValue(false)
                if (task.isSuccessful) {
                    _userLogged.postValue(true)
                } else {
                    _userLogged.postValue(false)
                }
            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Teste", "signInWithCredential:success")
                        _userLogged.postValue(true)
                    } else {
                        Log.w("Teste", "signInWithCredential:failure", task.exception)
                        _userLogged.postValue(false)
                    }
                }
    }
}