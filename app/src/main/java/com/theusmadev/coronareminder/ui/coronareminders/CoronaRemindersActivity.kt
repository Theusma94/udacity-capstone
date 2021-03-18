package com.theusmadev.coronareminder.ui.coronareminders

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivityCoronaRemindersBinding
import com.theusmadev.coronareminder.ui.signin.SignInActivity
import com.theusmadev.coronareminder.utils.AdCallback
import com.theusmadev.coronareminder.utils.AdHelper
import org.koin.android.ext.android.inject


class CoronaRemindersActivity: AppCompatActivity(), AdCallback {

    private val viewModel: MainViewModel by inject()

    private lateinit var binding: ActivityCoronaRemindersBinding
    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var adHelper: AdHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_corona_reminders)
        adHelper = AdHelper(this)
        val adRequest = AdRequest.Builder().build()
        binding.adBanner.loadAd(adRequest)

        navController = findNavController(R.id.nav_host_fragment)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.dashboard_fragment,
                R.id.create_reminder_fragment,
                R.id.history_fragment
        ))
        toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.nav_host_fragment))

        adHelper.setListener(this)
        adHelper.initInterstitialAd()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                viewModel.clearData()
                makeLogout()
            }
        }
        return true
    }

    private fun makeLogout() {
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut().addOnCompleteListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun interstitialFinalized(isCreated: Boolean) {
        if(isCreated) {
            adHelper.showInterstitialAd(this)
        }
    }

}