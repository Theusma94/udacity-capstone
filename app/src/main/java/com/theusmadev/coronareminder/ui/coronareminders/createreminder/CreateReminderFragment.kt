package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentCreateReminderBinding
import com.theusmadev.coronareminder.utils.AdHelper
import com.theusmadev.coronareminder.utils.CalendarListener
import com.theusmadev.coronareminder.utils.CalendarUtils
import org.koin.android.ext.android.inject

class CreateReminderFragment : Fragment(), CalendarListener {

    val viewModel: CreateReminderViewModel by inject()
    private val calendarUtils: CalendarUtils by inject()
    private lateinit var adHelper: AdHelper

    lateinit var binding: FragmentCreateReminderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_reminder, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewLifecycleOwner.lifecycle.addObserver(calendarUtils)
        binding.viewModel = viewModel

        adHelper = AdHelper(requireContext())

        calendarUtils.setUp(this)

        binding.shapeCalendar.setOnClickListener {
            calendarUtils.showPicker(requireContext())
        }

        setupObservers()
        adHelper.initInterstitialAd()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.reminderCreated.observe(viewLifecycleOwner, Observer { isCreated ->
            isCreated?.let {
                if(it) {
                    findNavController().navigate(CreateReminderFragmentDirections.actionCreateReminderFragmentToDashboardFragment())
                    adHelper.showInterstitialAd(requireActivity())
                    viewModel.onReminderCreated()
                    Toast.makeText(requireContext(), getString(R.string.reminder_created), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_on_create_reminder), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onDateAndHourSeted(dateAndHour: String) {
        binding.dateSelected.text = dateAndHour
    }
}