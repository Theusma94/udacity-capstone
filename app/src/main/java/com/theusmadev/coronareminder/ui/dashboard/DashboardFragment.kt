package com.theusmadev.coronareminder.ui.dashboard

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentDashboardBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject


class DashboardFragment : Fragment() {

    val viewModel: DashboardViewModel by inject()

    lateinit var binding: FragmentDashboardBinding
    lateinit var animator: ObjectAnimator
    lateinit var dialogBuilder: AlertDialog.Builder

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        animator = ObjectAnimator.ofFloat(binding.refreshCases, View.ROTATION, -360f, 0f)
        animator.duration = 1000
        animator.repeatCount = Animation.INFINITE
        dialogBuilder = AlertDialog.Builder(requireContext())

        viewModel.startJob()
        viewModel.getCoronaInfo()
        setupObservers()

        binding.spinnerCountry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
            ) {
                if(position > 0) {
                    val selected = (selectedItemView as TextView).text
                    viewModel.getCountryCoronaInfo(selected.toString())
                }
            }
        }

        binding.refreshCases.setOnClickListener {
            animator.start()
            viewModel.updateData()
        }

        binding.changeCountry.setOnClickListener {
            viewModel.clearSelectedCountry()
        }

        binding.moreAboutOption.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCountryDetailFragment())
        }

        binding.infoCovid.setOnClickListener {
            val viewToShow = generateView()
            dialogBuilder.setTitle(getString(R.string.about_api))
                    .setView(viewToShow)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
        }
        return binding.root
    }

    private fun generateView(): View {
        val layout = View.inflate(requireContext(), R.layout.message_about_api, null)
        layout.findViewById<TextView>(R.id.hyperlink).movementMethod = LinkMovementMethod.getInstance()
        return layout
    }

    private fun setupObservers() {
        viewModel.showListCountries.observe(viewLifecycleOwner, Observer {
            binding.cardCountry.visibility = View.GONE
            binding.spinnerContainer.visibility = View.VISIBLE
            binding.spinnerCountry.visibility = View.VISIBLE

            val adapter = ArrayAdapter<String>(requireContext(), R.layout.country_spinner_simple_item, it).apply {
                setDropDownViewResource(R.layout.country_spinner_dropdown_item)
            }
            binding.spinnerCountry.adapter = adapter
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if(!it) {
                animator.cancel()
            }
        })

        viewModel.countrySelected.observe(viewLifecycleOwner, Observer { isSelected ->
            isSelected?.let {
                binding.cardCountry.visibility = View.VISIBLE
                binding.spinnerContainer.visibility = View.GONE
                binding.spinnerCountry.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelJob()
    }
}