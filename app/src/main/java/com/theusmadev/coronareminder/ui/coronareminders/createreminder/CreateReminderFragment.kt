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
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class CreateReminderFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient
    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

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
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.startSaveGeofence.observe(viewLifecycleOwner, Observer { reminderItem ->
            addGeofence(reminderItem)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        removeGeofences()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(reminderDataItem: ReminderDataItem) {
        val geofence = Geofence.Builder()
            .setRequestId(reminderDataItem.id)
            .setCircularRegion(reminderDataItem.latitude!!,
                reminderDataItem.longitude!!,
                100f
            )
            .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.e("Add Geofence", geofence.requestId)
            }
            addOnFailureListener {
                Toast.makeText(requireContext(), "Error on create geofence",
                    Toast.LENGTH_SHORT).show()
                if ((it.message != null)) {
                    Log.w("", it.message ?: "Error")
                }
            }
        }
    }

    private fun removeGeofences() {
        if (!foregroundAndBackgroundLocationPermissionApproved()) {
            return
        }
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d("", "Geofence removed")
            }
            addOnFailureListener {
                Log.d("", "Failed on remove Geofence")
            }
        }
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    companion object {
        const val ACTION_GEOFENCE_EVENT = "CreateReminderFragment.locationreminder.ACTION_GEOFENCE_EVENT"
    }
}