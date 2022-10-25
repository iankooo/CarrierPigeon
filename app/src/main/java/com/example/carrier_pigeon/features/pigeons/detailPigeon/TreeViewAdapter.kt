package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.databinding.AdapterTreeViewItemBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

class TreeViewAdapter(private val context: Context?, val dataSet: List<Pigeon>) :
    RecyclerView.Adapter<TreeViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterTreeViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            with(dataSet[position]) {
                binding.ll.visible()
                binding.treeItemSeries.text =
                    context?.getString(R.string.country_series_format, country, series)
                binding.treeItemNickname.text = nickname
                binding.treeItemOwner.text = "Schipala Ianko"
                binding.treeItemColor.text = color
                binding.treeItemDetails.text = details
                this@apply.children = children
                treeViewAdapter = TreeViewAdapter(context, children)
                binding.treeItemRecyclerView.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    setHasFixedSize(true)
                    adapter = treeViewAdapter
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: AdapterTreeViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var treeViewAdapter: TreeViewAdapter? = null
        var children: List<Pigeon>? = null
    }
}
