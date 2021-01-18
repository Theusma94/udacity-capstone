package com.theusmadev.coronareminder.ui.coronareminders.selectlocation

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.theusmadev.coronareminder.BuildConfig
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.databinding.FragmentSelectLocationBinding
import com.theusmadev.coronareminder.ui.coronareminders.createreminder.CreateReminderViewModel
import org.koin.android.ext.android.inject

class SelectLocationFragment: Fragment(), OnMapReadyCallback {

    var currentPoi: PointOfInterest? = null
    var marker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentSelectLocationBinding
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    val viewModel: CreateReminderViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        setHasOptionsMenu(true)
//        setDisplayHomeAsUpEnabled(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.saveReminder.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    private fun onLocationSelected() {
        viewModel.selectedPOI.value = currentPoi
        viewModel.selectedLocation.value = currentPoi?.name
        findNavController().navigateUp()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()
    }

    private fun setMapStyle(map: GoogleMap) {
//        try {
//            // Customize the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            val success = map.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                    requireContext(),
//                    R.raw.map_style
//                )
//            )
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.")
//            }
//        } catch (e: Resources.NotFoundException) {
//            Log.e(TAG, "Can't find style. Error: ", e)
//        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentlocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener {location ->
                location?.let {
                    val homeLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15F))
                }
            }

    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            binding.saveReminder.visibility = View.VISIBLE
            currentPoi = poi
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker?.showInfoWindow()
        }
    }

    private fun requestPermissionsIfNeed() {
        if (!foregroundAndBackgroundLocationPermissionApproved()) {
            requestForegroundAndBackgroundLocationPermissions()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
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

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(29 )
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        Log.d(TAG, "Request foreground only location permission")
        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
            map.isMyLocationEnabled = true
            getCurrentlocation()
        }
        else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED))
        {
            // Permission denied.
            Snackbar.make(
                binding.fragmentSelectLocation,
                "deu merda", Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Settings") {
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            enableMyLocation()
        }

    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true, dataItem: ReminderDataItem? = null) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.fragmentSelectLocation,
                    "Location services must be enabled to use the app", Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence(dataItem = dataItem)
                }.show()
            }
        }
    }

    companion object {
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        const val TAG = "SelectLocationFragment"
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    }

}