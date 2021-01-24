package com.theusmadev.coronareminder.ui.coronareminders.createreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentCreateReminderBinding
import com.theusmadev.coronareminder.utils.CalendarListener
import com.theusmadev.coronareminder.utils.CalendarUtils
import org.koin.android.ext.android.inject

class CreateReminderFragment : Fragment(), CalendarListener {

    val viewModel: CreateReminderViewModel by inject()
    private val calendarUtils: CalendarUtils by inject()

    lateinit var binding: FragmentCreateReminderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_reminder, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewLifecycleOwner.lifecycle.addObserver(calendarUtils)
        binding.viewModel = viewModel

        calendarUtils.setUp(this)

        binding.shapeCalendar.setOnClickListener {
            calendarUtils.showPicker(requireContext())
        }

        return binding.root
    }

    override fun onDateAndHourSeted(dateAndHour: String) {
        binding.dateSelected.text = dateAndHour
    }
}