package com.example.carrier_pigeon.features.families.addFamily

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentAddOrEditFamilyBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AddOrEditFamilyFragment : BaseFragment(R.layout.fragment_add_or_edit_family) {
    private val binding by viewBinding(FragmentAddOrEditFamilyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
