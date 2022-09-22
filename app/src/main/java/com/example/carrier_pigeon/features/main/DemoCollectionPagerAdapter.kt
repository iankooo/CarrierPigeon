package com.example.carrier_pigeon.features.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.carrier_pigeon.features.families.FamilyFragment
import com.example.carrier_pigeon.features.pigeons.PigeonFragment

private const val NUM_TABS = 2

class DemoCollectionPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PigeonFragment()
            1 -> return FamilyFragment()
        }
        return PigeonFragment()
    }
}