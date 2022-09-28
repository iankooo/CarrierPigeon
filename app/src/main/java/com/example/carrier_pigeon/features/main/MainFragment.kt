package com.example.carrier_pigeon.features.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {
    companion object {
        private const val DEFAULT_PAGE = 0
    }

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentMainBinding::bind)

    private lateinit var adapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeLabel.text =
            getString(R.string.welcome_comma_first_name_of_user, sharedPrefsWrapper.getFirstName())

        val pagesArray = arrayOf(
            getString(R.string.pigeons),
            getString(R.string.families)
        )

        adapter = PagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pagesArray[position]
        }.attach()

        setControls()
    }

    private fun setControls() {
        binding.addButton.setOnClickListener {
            if (binding.viewPager.currentItem == DEFAULT_PAGE) {
                findNavController().navigate(MainFragmentDirections.mainToAddOrEditPigeon(null))
            } else {
                findNavController().navigate(MainFragmentDirections.mainToAddOrEditFamily(null))
            }
        }
        binding.pigeonsFlightsBtn.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToPigeonsFlights())
        }
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.mainToProfile())
        }
    }
}
