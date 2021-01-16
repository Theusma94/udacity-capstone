package com.theusmadev.coronareminder.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentDashboardBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject


class DashboardFragment : Fragment() {

    val viewModel: DashboardViewModel by inject()

    lateinit var binding: FragmentDashboardBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getCoronaInfo()
        setupObservers()

        binding.spinnerCountry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                binding.countryConfirmedCases.visibility = View.VISIBLE
                binding.countryDeathCases.visibility = View.VISIBLE
                binding.countryRecoveredCases.visibility = View.VISIBLE
                binding.countryStatsLabel.visibility = View.VISIBLE

                val selected = (selectedItemView as TextView).text
                viewModel.getCountryCoronaInfo(selected.toString())
            }
        }
        return binding.root
    }

    private fun setupObservers() {
        viewModel.showListCountries.observe(viewLifecycleOwner, Observer {
            binding.countryConfirmedCases.visibility = View.GONE
            binding.countryDeathCases.visibility = View.GONE
            binding.countryRecoveredCases.visibility = View.GONE
            binding.countryStatsLabel.visibility = View.GONE

            val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCountry.adapter = adapter
        })
    }
}