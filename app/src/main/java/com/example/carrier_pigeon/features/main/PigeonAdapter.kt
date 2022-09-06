package com.example.carrier_pigeon.features.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.databinding.ItemPigeonBinding
import com.example.carrier_pigeon.features.main.data.Pigeon

class PigeonAdapter(private val dataSet: ArrayList<Pigeon>) :
    RecyclerView.Adapter<PigeonAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPigeonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPigeonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.pigeonGender.isActivated = dataSet[position].gender == com.example.carrier_pigeon.app.Config.MALE
        holder.binding.llc.isActivated = dataSet[position].gender == com.example.carrier_pigeon.app.Config.MALE
        holder.binding.pigeonId.text = dataSet[position].id
        holder.binding.pigeonCountry.text = dataSet[position].country
        holder.binding.pigeonNickname.text = dataSet[position].nickname
        holder.binding.pigeonColor.text = dataSet[position].color
        holder.binding.pigeonDetails.text = dataSet[position].details
    }

    override fun getItemCount(): Int = dataSet.size

    fun getPigeonFromPosition(position: Int) = dataSet[position]

    fun notifyEditItem(activity: PigeonFragment, position: Int, requestCode: Int) {
//        val intent = Intent(context, AddHappyPlaceActivity::class.java)
//        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
//        activity.startActivityForResult(intent, requestCode)
//        notifyItemChanged(position)
    }

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}
