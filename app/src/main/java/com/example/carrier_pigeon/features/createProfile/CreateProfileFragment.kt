package com.example.carrier_pigeon.features.createProfile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentCreateProfileBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class CreateProfileFragment : BaseFragment(R.layout.fragment_create_profile) {
    private val binding by viewBinding(FragmentCreateProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveProfileBtn.setOnClickListener {
            findNavController().navigate(CreateProfileFragmentDirections.createProfileToPigeon())
        }
    }
}
