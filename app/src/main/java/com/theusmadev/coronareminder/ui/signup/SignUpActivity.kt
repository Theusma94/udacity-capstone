package com.theusmadev.coronareminder.ui.signup

import android.R.attr.password
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.data.User
import com.theusmadev.coronareminder.databinding.ActivitySignUpBinding
import org.koin.android.ext.android.inject


class SignUpActivity: AppCompatActivity() {

    private lateinit var activitySignUpBinding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    val viewModel: SignUpViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        activitySignUpBinding.viewModel = viewModel
        activitySignUpBinding.lifecycleOwner = this
        activitySignUpBinding.loadingStatus = false

        firebaseAuth = FirebaseAuth.getInstance()

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.showLoading.observe(this, Observer { isLoading ->
            isLoading?.let { isLoadingNotNull ->
                activitySignUpBinding.loadingStatus = isLoadingNotNull
                activitySignUpBinding.invalidateAll()
            }
        })

        viewModel.userCreated.observe(this, Observer { response ->
            response?.let { responseNotNull ->
                if(responseNotNull) {
                    onBackPressed()
                } else {
                    Toast.makeText(this, "Error on register!", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onClearData()
    }

}