package com.example.carrier_pigeon.features.main

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.Config
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
        with(dataSet[position]) {
            holder.binding.pigeonGender.isActivated = gender == Config.MALE
            holder.binding.mainRl.isActivated = gender == Config.MALE
            holder.binding.pigeonId.text = id
            holder.binding.pigeonCountry.text = country
            holder.binding.pigeonNickname.text = nickname
            holder.binding.pigeonColor.text = color
            holder.binding.pigeonDetails.text = details
            if (pigeonImage.isNullOrEmpty()) {
                holder.binding.pigeonImage.setBackgroundResource(R.drawable.ic_class_image_placeholder)
            } else {
                holder.binding.pigeonImage.setImageURI(Uri.parse(pigeonImage))
            }
            if (pigeonEyeImage.isNullOrEmpty()) {
                holder.binding.pigeonEyeImage.setBackgroundResource(R.drawable.ic_eye_image_placeholder)
            } else {
                holder.binding.pigeonEyeImage.setImageURI(Uri.parse(pigeonEyeImage))
            }
        }
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
