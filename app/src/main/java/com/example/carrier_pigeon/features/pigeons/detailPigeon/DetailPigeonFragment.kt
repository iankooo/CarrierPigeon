package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.databinding.FragmentDetailPigeonBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailPigeonFragment : BaseFragment(R.layout.fragment_detail_pigeon) {
    companion object {
        private const val PIGEON_CLICKED = "pigeon_clicked"
    }

    private val binding by viewBinding(FragmentDetailPigeonBinding::bind)
    private val pigeonViewModel by viewModels<PigeonViewModel>()

    private lateinit var pigeon: Pigeon
    private var mAdaptor: TreeViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pigeon = arguments?.getParcelable(PIGEON_CLICKED)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerTitle.addButton.invisible()
        binding.headerTitle.profileBtn.invisible()
        binding.headerTitle.backButton.visible()
        binding.headerTitle.exportToPdfBtn.visible()

        binding.headerTitle.welcomeLabel.text =
            getString(R.string.pedigree_pigeon, pigeon.country, pigeon.series)

        binding.headerTitle.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        pigeonViewModel.makeFamilyTreeFor(pigeon)
        mAdaptor = TreeViewAdapter(context, listOf(pigeon))
        binding.aici.treeItemRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdaptor
        }
    }
}
