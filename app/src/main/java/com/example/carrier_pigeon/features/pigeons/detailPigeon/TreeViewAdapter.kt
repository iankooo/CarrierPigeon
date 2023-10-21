package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.NodeBaseLayoutBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.model.NodeModel

class TreeViewAdapter(val sharedPrefsWrapper: SharedPrefsWrapper) :
    TreeViewAdapter<Pigeon>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        node: NodeModel<Pigeon>
    ): TreeViewHolder<Pigeon> {
        val nodeBinding: NodeBaseLayoutBinding =
            NodeBaseLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TreeViewHolder(nodeBinding.root, node)
    }

    override fun onBindViewHolder(holder: TreeViewHolder<Pigeon>) {
        val itemView = holder.view
        val node: NodeModel<Pigeon> = holder.node
        itemView.apply {
            val s: String = node.value.series + " " + node.value.gender
            findViewById<TextView>(R.id.tree_item_series).text = s
            findViewById<TextView>(R.id.tree_item_nickname).text = node.value.nickname
            findViewById<TextView>(R.id.tree_item_owner).text = context?.getString(
                R.string.two_strings_format,
                sharedPrefsWrapper.getLastName(),
                sharedPrefsWrapper.getFirstName()
            )
            findViewById<TextView>(R.id.tree_item_color).text = node.value.color
            findViewById<TextView>(R.id.tree_item_details).text = node.value.details
        }
    }

    override fun onDrawLine(drawInfo: DrawInfo?): BaseLine? {
        return null
    }
}
