package com.theusmadev.coronareminder.ui.coronareminders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.ActivityCoronaRemindersBinding

class CoronaRemindersActivity: AppCompatActivity() {

    lateinit var binding: ActivityCoronaRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_corona_reminders)
        binding.bottomNavigation.setupWithNavController(Navigation.findNavController(this, R.id.nav_host_fragment))
    }

}