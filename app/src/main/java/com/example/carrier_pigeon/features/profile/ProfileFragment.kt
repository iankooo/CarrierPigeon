package com.example.carrier_pigeon.features.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.shortToast
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentProfileBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {
    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.headerTitle.welcomeLabel.setText(R.string.profile)
        binding.headerTitle.addButton.invisible()
        binding.headerTitle.profileBtn.invisible()
        binding.headerTitle.backButton.visible()
        binding.headerTitle.exportToPdfBtn.invisible()

        binding.headerTitle.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.phoneNumberEt.setText(sharedPrefsWrapper.getPhoneNumber())
        binding.firstNameEt.setText(sharedPrefsWrapper.getFirstName())
        binding.lastNameEt.setText(sharedPrefsWrapper.getLastName())
        binding.homeAddressEt.setText(sharedPrefsWrapper.getHomeAddress())
        binding.emailAddressEt.setText(sharedPrefsWrapper.getEmailAddress())

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
                    saveEmailAddress(binding.emailAddressEt.text.toString())
                }
                findNavController().popBackStack()
            }
        }
    }
}
