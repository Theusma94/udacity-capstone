package com.theusmadev.coronareminder.ui.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivitySignInBinding
import com.theusmadev.coronareminder.ui.coronareminders.CoronaRemindersActivity
import com.theusmadev.coronareminder.ui.signup.SignUpActivity
import org.koin.android.ext.android.inject

class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by inject()

    private lateinit var activitySignInBinding: ActivitySignInBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGoogleSignIn()

        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        activitySignInBinding.viewModel = viewModel

        activitySignInBinding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        activitySignInBinding.siginOptionGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun setupGoogleSignIn() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Teste", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("Teste", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Teste", "signInWithCredential:success")
                    val user = viewModel.firebaseAuth.currentUser
                    startActivity(Intent(this, CoronaRemindersActivity::class.java))
                } else {
                    Log.w("Teste", "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val user = viewModel.firebaseAuth.currentUser
        if(user != null) {
            Log.d("Teste","user ${user.email} logged")
            startActivity(Intent(this, CoronaRemindersActivity::class.java))
        } else {
            Log.d("Teste", "user not logged")
        }
    }

    companion object {
        private const val RC_SIGN_IN = 0
    }

}