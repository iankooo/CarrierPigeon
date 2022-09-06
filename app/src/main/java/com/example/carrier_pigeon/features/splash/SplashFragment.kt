package com.example.carrier_pigeon.features.splash

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash) {
    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sharedPrefsWrapper.getPhoneNumber().isEmpty()) {
            findNavController().navigate(SplashFragmentDirections.splashToCreateProfile())
        } else {
            findNavController().navigate(SplashFragmentDirections.splashToPigeon())
        }
    }
}
