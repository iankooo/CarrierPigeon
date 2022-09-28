package com.example.carrier_pigeon.features.pigeons

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.Config.MALE
import com.example.carrier_pigeon.app.UiThreadPoster
import com.example.carrier_pigeon.app.utils.DATE_FORMAT
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
    private val uiThreadPoster: UiThreadPoster
) : RecyclerView.Adapter<PigeonAdapter.ViewHolder>() {

    private var dataSet = emptyList<Pigeon>().toMutableList()

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
        viewHolder.apply {
            with(dataSet[position]) {
                itemView.setOnClickListener { onPigeonClicked.invoke(this) }
                binding.pigeonGender.isActivated = gender == MALE
                binding.mainRl.isActivated = gender == MALE
                binding.pigeonSeries.text = series
                binding.pigeonCountry.text = country
                binding.pigeonNickname.text = nickname
                binding.pigeonColor.text = color

                if (pigeonImage.isNullOrEmpty()) {
                    binding.pigeonImage.setBackgroundResource(R.drawable.ic_class_image_placeholder)
                } else {
                    binding.pigeonImage.setImageURI(Uri.parse(pigeonImage))
                }
                if (pigeonEyeImage.isNullOrEmpty()) {
                    binding.pigeonEyeImage.invisible()
                } else {
                    binding.pigeonEyeImage.setImageURI(Uri.parse(pigeonEyeImage))
                }
                val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
                val date = LocalDate.parse(dateOfBirth, formatter)
                val p = Period.between(date, LocalDate.now())
                binding.pigeonDateOfBirth.text = context?.getString(
                    R.string.pigeon_age,
                    p.years.toString(),
                    p.months.toString(),
                    p.days.toString()
                )

                binding.pigeonDetails.setOnClickListener {
                    onDetailsClicked(this@apply, this)
                }

                if (firstVaccine == 1) {
                    binding.firstVaccine.visible()
                    binding.vaccineTv.visible()
                    binding.secondVaccine.invisible()
                } else {
                    binding.firstVaccine.invisible()
                    binding.vaccineTv.invisible()
                }
                if (secondVaccine == 1 && viewHolder.binding.firstVaccine.isVisible) {
                    binding.secondVaccine.visible()
                    binding.thirdVaccine.invisible()
                } else {
                    binding.secondVaccine.invisible()
                }
                if (thirdVaccine == 1 && viewHolder.binding.secondVaccine.isVisible) {
                    binding.thirdVaccine.visible()
                } else {
                    binding.thirdVaccine.invisible()
                }
            }
        }
    }

    private fun onDetailsClicked(
        viewHolder1: ViewHolder,
        pigeon: Pigeon
    ) {
        viewHolder1.binding.pigeonDetails.text = pigeon.details
        Timer().schedule(
            timerTask {
                uiThreadPoster.post {
                    viewHolder1.binding.pigeonDetails.text =
                        context?.getString(R.string.click_to_see_details)
                }
            },
            6000
        )
    }

    override fun getItemCount(): Int = dataSet.size

    fun getPigeonFromPosition(position: Int) = dataSet[position]

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    internal fun setItems(items: List<Pigeon>) {
        this.dataSet = items.toMutableList()
        notifyDataSetChanged()
    }
}
