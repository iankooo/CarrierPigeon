package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentDetailPigeonBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Node
import java.util.*
import kotlin.collections.ArrayList

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
        binding.welcomeLabel.text =
            getString(R.string.pedigree_pigeon, pigeon.country, pigeon.series)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val rootList: MutableList<Pigeon> = ArrayList()
        pigeonViewModel.allPigeons.observe(viewLifecycleOwner) {
            // val roots: List<Pigeon> = pigeonViewModel.getRoots(0)
            for (pigeon in it) {
                pigeonViewModel.makeFamilyTreeFor(pigeon)
                rootList.add(pigeon)
            }
            mAdaptor = TreeViewAdapter(rootList.filter { it.id == pigeon.id })

            binding.aici.treeItemRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdaptor
            }
        }
    }
}
