package com.example.carrier_pigeon.features.pigeons.pigeonDetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentPigeonDetailBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class PigeonDetailFragment : BaseFragment(R.layout.fragment_pigeon_detail) {
    private var pigeon: Pigeon? = null
    private val binding by viewBinding(FragmentPigeonDetailBinding::bind)

    companion object {
        private const val PIGEON_CLICKED = "pigeon_clicked"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pigeon = arguments?.getParcelable(PIGEON_CLICKED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeLabel.text =
            getString(R.string.pedigree_pigeon, pigeon?.country, pigeon?.series)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
