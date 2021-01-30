package com.theusmadev.coronareminder.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.theusmadev.coronareminder.data.User

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
        } else {
            _showLoading.value = false
            _userLogged.value = false
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
                        _userLogged.postValue(true)
                    } else {
                        _userLogged.postValue(false)
                    }
                }
    }
}