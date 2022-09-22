package com.example.carrier_pigeon.features.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val animalsArray = arrayOf(
    "Pigeons",
    "Families"
)

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {
    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeLabel.text =
            getString(R.string.welcome_comma_first_name_of_user, sharedPrefsWrapper.getFirstName())

        val adapter = DemoCollectionPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = animalsArray[position]
        }.attach()

        setControls()
    }

    private fun setControls() {
        binding.addButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToAddOrEditPigeon(null))
        }
        binding.pigeonsFlightsBtn.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToPigeonsFlights())
        }
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToProfile())
        }
    }
}
