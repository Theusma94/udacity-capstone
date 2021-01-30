package com.theusmadev.coronareminder.ui.countrydetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.data.local.model.CoronaStateData
import com.theusmadev.coronareminder.databinding.ItemCountryInfoBinding
import com.theusmadev.coronareminder.ui.base.DataBoundListAdapter

class CoronaAdapter: DataBoundListAdapter<CoronaStateData, ItemCountryInfoBinding>(
        diffCallback = object: DiffUtil.ItemCallback<CoronaStateData>() {
            override fun areItemsTheSame(oldItem: CoronaStateData, newItem: CoronaStateData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CoronaStateData, newItem: CoronaStateData): Boolean {
                return oldItem.region == newItem.region
            }
        }
) {
    override fun createBinding(parent: ViewGroup): ItemCountryInfoBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_country_info,
                parent,
                false
        )
    }

    override fun bind(binding: ItemCountryInfoBinding, item: CoronaStateData) {
        binding.coronaState = item
    }
}