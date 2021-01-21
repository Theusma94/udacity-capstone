package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.databinding.FragmentCreateReminderBinding
import com.theusmadev.coronareminder.ui.coronareminders.geofence.GeofenceBroadcastReceiver
import com.theusmadev.coronareminder.utils.GeofenceUtils
import com.theusmadev.coronareminder.utils.GeofenceUtils.Companion.ACTION_GEOFENCE_EVENT
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class CreateReminderFragment : Fragment() {

    private val geofenceUtils: GeofenceUtils by inject()

//    private lateinit var geofencingClient: GeofencingClient
//
//    private val geofencePendingIntent: PendingIntent by lazy {
//        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
//        intent.action = ACTION_GEOFENCE_EVENT
//        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }

    val viewModel: CreateReminderViewModel by inject()

    lateinit var binding: FragmentCreateReminderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_reminder, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.switchFavorite.isChecked = viewModel.isFavorite
        binding.buttonSelectLocation.setOnClickListener {
            findNavController().navigate(
                CreateReminderFragmentDirections.actionCreateReminderFragmentToSelectLocationFragment())
        }

        geofenceUtils.setup(requireContext())
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.startSaveGeofence.observe(viewLifecycleOwner, Observer { reminderItem ->
            geofenceUtils.addGeofence(requireContext(), reminderItem)
        })
    }
}