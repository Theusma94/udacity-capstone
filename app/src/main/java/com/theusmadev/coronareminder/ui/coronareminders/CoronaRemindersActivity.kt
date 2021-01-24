package com.theusmadev.coronareminder.ui.coronareminders

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivityCoronaRemindersBinding
import com.theusmadev.coronareminder.ui.coronareminders.createreminder.favorites.RemindersViewModel
import org.koin.android.ext.android.inject

class CoronaRemindersActivity: AppCompatActivity() {

    lateinit var binding: ActivityCoronaRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_corona_reminders)
        binding.bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.nav_host_fragment))
    }

}