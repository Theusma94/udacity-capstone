package com.theusmadev.coronareminder.ui.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivitySignInBinding
import org.koin.android.ext.android.inject

class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by inject()

    private lateinit var activitySignInBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        activitySignInBinding.viewModel = viewModel

        activitySignInBinding.signUpText.setOnClickListener {

        }
    }
}