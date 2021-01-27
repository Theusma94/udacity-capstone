package com.theusmadev.coronareminder.ui.coronareminders.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.data.local.model.ReminderDataItem
import com.theusmadev.coronareminder.databinding.ItemReminderBinding
import com.theusmadev.coronareminder.ui.base.DataBoundListAdapter

class RemindersAdapter: DataBoundListAdapter<ReminderDataItem, ItemReminderBinding>(
        diffCallback = object: DiffUtil.ItemCallback<ReminderDataItem>() {
            override fun areItemsTheSame(oldItem: ReminderDataItem, newItem: ReminderDataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ReminderDataItem, newItem: ReminderDataItem): Boolean {
                return oldItem.requestCode == newItem.requestCode
            }

        }) {

    override fun createBinding(parent: ViewGroup): ItemReminderBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_reminder,
                parent,
                false
        )
    }

    override fun bind(binding: ItemReminderBinding, item: ReminderDataItem) {
        binding.item = item
    }
}