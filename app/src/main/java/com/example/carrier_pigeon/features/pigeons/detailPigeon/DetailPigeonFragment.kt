package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentDetailPigeonBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.layout.RightTreeLayoutManager
import com.gyso.treeview.layout.TreeLayoutManager
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.line.StraightLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailPigeonFragment : BaseFragment(R.layout.fragment_detail_pigeon) {
    companion object {
        private const val PIGEON_CLICKED = "pigeon_clicked"
    }

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentDetailPigeonBinding::bind)
    private val pigeonViewModel by viewModels<PigeonViewModel>()

    private lateinit var pigeon: Pigeon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pigeon = arguments?.getParcelable(PIGEON_CLICKED)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setControls()

        pigeonViewModel.makeFamilyTreeFor(pigeon)
        val line: BaseLine =
            StraightLine(ResourcesCompat.getColor(resources, R.color.light_blue, null), 8)
        val treeLayoutManager: TreeLayoutManager =
            RightTreeLayoutManager(context, 50, 20, line)

        val adapter: TreeViewAdapter<*> = TreeViewAdapter(sharedPrefsWrapper)
        binding.baseTreeView.apply {
            this.adapter = adapter
            setTreeLayoutManager(treeLayoutManager)
        }

        val treeModel: TreeModel<Pigeon> = TreeModel(NodeModel(pigeon))

        createTree(treeModel, treeModel.rootNode, pigeon.children)
        adapter.treeModel = treeModel

        val editor: TreeViewEditor = binding.baseTreeView.editor
        editor.focusMidLocation()
        editor.requestMoveNodeByDragging(false)
    }

    private fun createTree(
        treeModel: TreeModel<Pigeon>,
        currentNode: NodeModel<Pigeon>,
        children: List<Pigeon>
    ) {
        for (index in 0..children.lastIndex) {
            val nodeChildren = NodeModel(children[index])
            treeModel.addNode(currentNode, nodeChildren)
            createTree(treeModel, nodeChildren, children[index].children)
        }
    }

    private fun setControls() {
        binding.headerTitle.apply {
            addButton.invisible()
            profileBtn.invisible()
            backButton.visible()
            exportToPdfBtn.visible()

            welcomeLabel.text =
                getString(R.string.pedigree_pigeon, pigeon.country, pigeon.series, pigeon.gender)

            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        binding.refresh.setOnClickListener {
            val editor: TreeViewEditor = binding.baseTreeView.editor
            editor.focusMidLocation()
        }
    }
}
