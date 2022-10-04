package com.example.carrier_pigeon.features.pigeons.detailPigeon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.databinding.AdapterTreeViewItemBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

class TreeViewAdapter(val dataSet: List<Pigeon>) :
    RecyclerView.Adapter<TreeViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterTreeViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: AdapterTreeViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var treeViewAdapter: TreeViewAdapter? = null
        private var children: List<Pigeon>? = null

        fun bind(familyMember: Pigeon) {
            val n: String =
                familyMember.country + " " + familyMember.series
            binding.ll.visible()
            binding.treeItemSeries.text = n
            binding.treeItemNickname.text = familyMember.nickname
            binding.treeItemOwner.text = "Schipala Ianko"
            binding.treeItemColor.text = familyMember.color
            binding.treeItemDetails.text = familyMember.details
            children = familyMember.children
            treeViewAdapter = TreeViewAdapter(children!!)
            binding.treeItemRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                setHasFixedSize(true)
                adapter = treeViewAdapter
            }
        }
    }
}
