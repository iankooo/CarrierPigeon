package com.example.carrier_pigeon.features.pigeons.addPigeon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.features.pigeons.data.Pigeon

class TreeViewAdapter(dataSet: List<Pigeon>?) :
    RecyclerView.Adapter<TreeViewAdapter.ViewHolder>() {
    private var localDataSet: List<Pigeon>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_tree_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(localDataSet!![position])
    }

    override fun getItemCount(): Int {
        return localDataSet!!.size
    }

    fun setDataSet(dataSet: List<Pigeon>?) {
        localDataSet = dataSet
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView
        private val imageButton: ImageButton
        private var treeViewAdapter: TreeViewAdapter? = null
        private val recyclerView: RecyclerView
        private var children: List<Pigeon>? = null
        fun bind(familyMember: Pigeon) {
            val n: String =
                familyMember.country + " " + familyMember.series
            name.text = n
            name.setOnClickListener { Log.d("TreeViewAdapter", "Hello from name") }
            imageButton.setOnClickListener { v: View? ->
                Log.d("TreeViewAdapter", "Hello from image button")
                when (recyclerView.visibility) {
                    View.GONE -> recyclerView.visibility = View.VISIBLE
                    View.VISIBLE -> recyclerView.visibility = View.GONE
                }
            }
            children = familyMember.children
            treeViewAdapter = TreeViewAdapter(children)
            recyclerView.adapter = treeViewAdapter
        }

        init {
            name = itemView.findViewById(R.id.tree_item_name)
            imageButton = itemView.findViewById(R.id.tree_item_button)
            recyclerView = itemView.findViewById(R.id.tree_item_recycler_view)
            val manager: RecyclerView.LayoutManager = LinearLayoutManager(itemView.context)
            recyclerView.layoutManager = manager
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    itemView.context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }

    init {
        localDataSet = dataSet
    }
}