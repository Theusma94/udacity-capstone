package com.theusmadev.coronareminder.ui.coronareminders.createreminder.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentFavoritesBinding
import org.koin.android.ext.android.inject

class FavoritesFragment : Fragment() {

    val viewModel: RemindersViewModel by inject()

    lateinit var binding: FragmentFavoritesBinding
    lateinit var adapter: RemindersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
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