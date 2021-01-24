package com.theusmadev.coronareminder.ui.dashboard

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
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
    lateinit var animator: ObjectAnimator

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        animator = ObjectAnimator.ofFloat(binding.refreshCases, View.ROTATION, -360f, 0f)
        animator.duration = 1000
        animator.repeatCount = Animation.INFINITE

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
                if(position > 0) {
                    binding.contentCountry.visibility = View.VISIBLE
                    binding.spinnerContainer.visibility = View.GONE
                    binding.spinnerCountry.visibility = View.GONE

                    val selected = (selectedItemView as TextView).text
                    viewModel.getCountryCoronaInfo(selected.toString())
                }
            }
        }

        binding.refreshCases.setOnClickListener {
            animator.start()
            viewModel.updateData()
        }
        return binding.root
    }

    private fun setupObservers() {
        viewModel.showListCountries.observe(viewLifecycleOwner, Observer {
            binding.contentCountry.visibility = View.GONE
            binding.spinnerContainer.visibility = View.VISIBLE
            binding.spinnerCountry.visibility = View.VISIBLE

            val adapter = ArrayAdapter<String>(requireContext(), R.layout.country_spinner_simple_item, it)
            adapter.setDropDownViewResource(R.layout.country_spinner_dropdown_item)
            binding.spinnerCountry.adapter = adapter
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if(!it) {
                animator.cancel()
            }
        })
    }
}