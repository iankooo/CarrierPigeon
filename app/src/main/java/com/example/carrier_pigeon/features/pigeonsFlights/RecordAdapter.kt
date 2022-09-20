package com.example.carrier_pigeon.features.pigeonsFlights

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.utils.dateToShortDate
import com.example.carrier_pigeon.app.utils.gone
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.databinding.ItemRecordBinding
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordAdapter(data: ArrayList<Record>, context: Context?) :
    ArrayAdapter<Record?>(
        context!!, R.layout.item_record,
        data as List<Record?>
    ),
    View.OnClickListener {

    inner class ViewHolder(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root)

    private var dataSet: ArrayList<Record>
    var mContext: Context

    init {
        dataSet = data
        mContext = context!!
    }

    override fun onClick(v: View) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRecordBinding
        val dataModel: Record? = getItem(position)
        var row = convertView

        if (row == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = ItemRecordBinding.inflate(inflater, parent, false)
            row = binding.root
        } else {
            binding = ItemRecordBinding.bind(row)
        }
        with(dataModel) {
            if (this != null) {
                binding.nr.text = context.getString(R.string.nr_of_flight_format, position + 1)

                binding.ringSeries.text =
                    context.getString(R.string.ring_series_format, country, dateToShortDate(), series)
                binding.gender.text = gender
                binding.color.text = color

                binding.vaccineTv.gone()
                binding.firstVaccine.visible()
                binding.secondVaccine.visible()
                binding.thirdVaccine.visible()

                binding.firstVaccine.isChecked = firstVaccine == 1
                binding.secondVaccine.isChecked = secondVaccine == 1
                binding.thirdVaccine.isChecked = thirdVaccine == 1
            }
        }
        return row
    }

    fun addRecords(records: ArrayList<Record>) {
        for (i in records) {
            if (!dataSet.contains(i)) {
                dataSet.add(i)
            }
        }
        notifyDataSetChanged()
    }

    fun getAllRecords() = dataSet

    fun getRecordFromPosition(position: Int) = dataSet[position]

    fun addRecord(record: Record) {
        dataSet.add(record)
    }

    fun removeRecord(record: Record) {
        dataSet.remove(record)
    }

    internal fun setItems(items: List<Record>) {
        this.dataSet = ArrayList(items)
        notifyDataSetChanged()
    }
}
