package com.theusmadev.coronareminder.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.data.repository.ReminderRepository
import com.theusmadev.coronareminder.ui.coronareminders.geofence.GeofenceBroadcastReceiver
import java.util.concurrent.TimeUnit

class GeofenceUtils(val permissionsUtils: PermissionsUtils) {

    private var geofencingClient: GeofencingClient? = null
    private var geofencePendingIntent: PendingIntent? = null

    fun setup(context: Context) {
        if(geofencePendingIntent == null) {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            intent.action = ACTION_GEOFENCE_EVENT
            geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if(geofencingClient == null) {
            geofencingClient = LocationServices.getGeofencingClient(context)
        }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(context: Context, reminderDataItem: ReminderDataItem) {
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

        geofencingClient?.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.e("Add Geofence", geofence.requestId)
            }
            addOnFailureListener {
                Toast.makeText(context, "Error on create geofence",
                    Toast.LENGTH_SHORT).show()
                if ((it.message != null)) {
                    Log.w("", it.message ?: "Error")
                }
            }
        }
    }

    fun removeGeofences() {
        if (!permissionsUtils.foregroundAndBackgroundLocationPermissionApproved()) {
            return
        }
        Log.d("Geofence", "geofence intent $geofencePendingIntent")
        geofencingClient?.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d("Geofence", "Geofence removed")
            }
            addOnFailureListener {
                Log.d("Geofence", "Failed on remove Geofence")
            }
        }
    }

    companion object {
        const val ACTION_GEOFENCE_EVENT = "CreateReminderFragment.locationreminder.ACTION_GEOFENCE_EVENT"
    }
}