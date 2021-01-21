package com.theusmadev.coronareminder.ui.coronareminders

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivityCoronaRemindersBinding
import com.theusmadev.coronareminder.ui.coronareminders.createreminder.favorites.RemindersViewModel
import com.theusmadev.coronareminder.ui.coronareminders.geofence.GeofenceBroadcastReceiver
import com.theusmadev.coronareminder.utils.GeofenceUtils
import org.koin.android.ext.android.inject

class CoronaRemindersActivity: AppCompatActivity() {

    val geofenceUtils: GeofenceUtils by inject()
    val viewModel: RemindersViewModel by inject()

    lateinit var binding: ActivityCoronaRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_corona_reminders)
        binding.bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.nav_host_fragment))

        geofenceUtils.setup(this)

        setupObservers()
        viewModel.getRemindersFromDb()
    }

    private fun setupObservers() {
        viewModel.listOfReminders.observe(this, Observer { reminders ->
            Log.d("Geofence", "geofence activity")
            reminders.forEach { reminder ->
                geofenceUtils.addGeofence(this, reminder)
            }
            viewModel.listOfReminders.removeObservers(this)
        })
    }

    override fun onDestroy() {
        geofenceUtils.removeGeofences()
        super.onDestroy()
    }

}