package com.example.carrier_pigeon.features.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentMainBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {
    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentMainBinding::bind)

    private lateinit var pagerAdapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerTitle.welcomeLabel.text =
            getString(R.string.welcome_comma_first_name_of_user, sharedPrefsWrapper.getFirstName())

        pagerAdapter = PagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.apply {
            adapter = pagerAdapter
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

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
