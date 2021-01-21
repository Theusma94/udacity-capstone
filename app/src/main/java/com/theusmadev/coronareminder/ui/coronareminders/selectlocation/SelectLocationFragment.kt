package com.theusmadev.coronareminder.ui.coronareminders.selectlocation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.theusmadev.coronareminder.BuildConfig
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentSelectLocationBinding
import com.theusmadev.coronareminder.ui.coronareminders.createreminder.CreateReminderViewModel
import com.theusmadev.coronareminder.utils.PermissionsUtils
import org.koin.android.ext.android.inject

class SelectLocationFragment: Fragment(), OnMapReadyCallback {

    val viewModel: CreateReminderViewModel by inject()
    val permissionsUtils: PermissionsUtils by inject()

    var currentPoi: PointOfInterest? = null
    var marker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap

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
        enableMyLocation()
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
        if (!permissionsUtils.foregroundAndBackgroundLocationPermissionApproved()) {
            permissionsUtils.requestForegroundAndBackgroundLocationPermissions(this)
        } else {
            permissionsUtils.checkDeviceLocationSettingsAndStartGeofence(requireActivity() as AppCompatActivity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (permissionsUtils.foregroundAndBackgroundLocationPermissionApproved()) {
            permissionsUtils.checkDeviceLocationSettingsAndStartGeofence(requireActivity() as AppCompatActivity)
            map.isMyLocationEnabled = true
            getCurrentlocation()
        }
        else {
            permissionsUtils.requestForegroundAndBackgroundLocationPermissions(this)
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

    companion object {
        const val TAG = "SelectLocationFragment"
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    }

}