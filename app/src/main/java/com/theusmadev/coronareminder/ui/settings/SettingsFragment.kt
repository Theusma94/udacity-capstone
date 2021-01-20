package com.theusmadev.coronareminder.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.databinding.FragmentSettingsBinding
import com.theusmadev.coronareminder.ui.signin.SignInActivity

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }
        return binding.root
    }

}