package com.example.carrier_pigeon.features.createProfile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.shortToast
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentCreateProfileBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateProfileFragment : BaseFragment(R.layout.fragment_create_profile) {
    private val binding by viewBinding(FragmentCreateProfileBinding::bind)

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveProfileBtn.setOnClickListener {
            if (binding.phoneNumberEt.text.toString().isEmpty()) {
                shortToast(getString(R.string.phone_number_cannot_be_empty))
            } else if (binding.firstNameEt.text.toString().isEmpty()) {
                shortToast(getString(R.string.first_name_cannot_be_empty))
            } else if (binding.lastNameEt.text.toString().isEmpty()) {
                shortToast(getString(R.string.last_name_cannot_be_empty))
            } else if (binding.homeAddressEt.text.toString().isEmpty()) {
                shortToast(getString(R.string.home_address_cannot_be_empty))
            } else {
                with(sharedPrefsWrapper) {
                    savePhoneNumber(binding.phoneNumberEt.text.toString())
                    saveFirstName(binding.firstNameEt.text.toString())
                    saveLastName(binding.lastNameEt.text.toString())
                    saveHomeAddress(binding.homeAddressEt.text.toString())
                }
                findNavController().navigate(CreateProfileFragmentDirections.createProfileToPigeon())
            }
        }
    }
}
