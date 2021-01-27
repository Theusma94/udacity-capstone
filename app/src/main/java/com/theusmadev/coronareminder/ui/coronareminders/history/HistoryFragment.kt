package com.theusmadev.coronareminder.ui.coronareminders.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentHistoryBinding
import org.koin.android.ext.android.inject

class HistoryFragment : Fragment() {

    val viewModel: RemindersViewModel by inject()

    lateinit var binding: FragmentHistoryBinding
    lateinit var adapter: RemindersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = RemindersAdapter()
        binding.listReminder.adapter = adapter
        setupObservers()
        viewModel.getRemindersFromDb()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.listOfReminders.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}