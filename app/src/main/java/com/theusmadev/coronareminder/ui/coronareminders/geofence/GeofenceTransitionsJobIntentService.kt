package com.theusmadev.coronareminder.ui.coronareminders.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {


    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                    context,
                    GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                    intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        geofenceEvent.triggeringGeofences.forEach(::sendNotification)
    }

    private fun sendNotification(geofence: Geofence) {
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            Log.w("Teste","geofence ${geofence.requestId} found")
        }
    }

}