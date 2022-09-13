package com.example.carrier_pigeon.features.pigeons

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.Config
import com.example.carrier_pigeon.app.UiThreadPoster
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.databinding.ItemPigeonBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timerTask

class PigeonAdapter(
    private val context: Context?,
    private val onPigeonClicked: (Pigeon) -> Unit,
    private val dataSet: ArrayList<Pigeon>,
    private val uiThreadPoster: UiThreadPoster
) :
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

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { onPigeonClicked.invoke(dataSet[position]) }
        with(dataSet[position]) {
            viewHolder.binding.pigeonGender.isActivated = gender == Config.MALE
            viewHolder.binding.mainRl.isActivated = gender == Config.MALE
            viewHolder.binding.pigeonSeries.text = series
            viewHolder.binding.pigeonCountry.text = country
            viewHolder.binding.pigeonNickname.text = nickname
            viewHolder.binding.pigeonColor.text = color

            if (pigeonImage.isNullOrEmpty()) {
                viewHolder.binding.pigeonImage.setBackgroundResource(R.drawable.ic_class_image_placeholder)
            } else {
                viewHolder.binding.pigeonImage.setImageURI(Uri.parse(pigeonImage))
            }
            if (pigeonEyeImage.isNullOrEmpty()) {
                viewHolder.binding.pigeonEyeImage.invisible()
            } else {
                viewHolder.binding.pigeonEyeImage.setImageURI(Uri.parse(pigeonEyeImage))
            }
            val formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy")
            val date = LocalDate.parse(dateOfBirth, formatter)
            val p = Period.between(date, LocalDate.now())
            viewHolder.binding.pigeonDateOfBirth.text = context?.getString(
                R.string.pigeon_age,
                p.years.toString(),
                p.months.toString(),
                p.days.toString()
            )

            if (firstVaccine == 1) {
                viewHolder.binding.firstVaccine.visible()
                viewHolder.binding.vaccineTv.visible()
            }
            if (secondVaccine == 1 && viewHolder.binding.firstVaccine.isVisible) {
                viewHolder.binding.secondVaccine.visible()
                viewHolder.binding.secondVaccine.visible()
            }
            if (thirdVaccine == 1 && viewHolder.binding.secondVaccine.isVisible) {
                viewHolder.binding.thirdVaccine.visible()
                viewHolder.binding.thirdVaccine.visible()
            }

            viewHolder.binding.pigeonDetails.setOnClickListener {
                viewHolder.binding.pigeonDetails.text = details
                Timer().schedule(
                    timerTask {
                        uiThreadPoster.post {
                            viewHolder.binding.pigeonDetails.text =
                                context?.getString(R.string.click_to_see_details)
                        }
                    },
                    6000
                )
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun getPigeonFromPosition(position: Int) = dataSet[position]

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}
