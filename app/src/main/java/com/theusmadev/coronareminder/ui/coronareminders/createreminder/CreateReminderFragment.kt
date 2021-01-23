package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentCreateReminderBinding
import com.theusmadev.coronareminder.utils.GeofenceUtils
import org.koin.android.ext.android.inject
import java.util.*

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
//            findNavController().navigate(
//                CreateReminderFragmentDirections.actionCreateReminderFragmentToSelectLocationFragment())
//           DatePickerDialog(requireContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
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
    val calendar = Calendar.getInstance()
    var date: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener {
        view, year, monthOfYear, dayOfMonth ->
    }
}