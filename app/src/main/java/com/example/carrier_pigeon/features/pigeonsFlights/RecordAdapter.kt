package com.example.carrier_pigeon.features.pigeonsFlights

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.carrier_pigeon.databinding.ItemRecordBinding

class RecordAdapter(data: ArrayList<Record>, context: Context?) :
    ArrayAdapter<Record?>(
        context!!, com.example.carrier_pigeon.R.layout.item_record,
        data as List<Record?>
    ),
    View.OnClickListener {
    private val dataSet: ArrayList<Record>
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
                binding.nr.text = nr
                binding.ringSeries.text = "$country $dateOfBirth $series"
                binding.gender.text = gender
                binding.color.text = color
                binding.vaccine.text = vaccine
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

    fun getAllRecords() = dataSet.map { it.series }

    fun removeRecords(removedPigeons: ArrayList<Record>?) {
        if (!removedPigeons.isNullOrEmpty()) {
            for (i in removedPigeons) {
                if (dataSet.contains(i)) {
                    dataSet.remove(i)
                }
            }
            notifyDataSetChanged()
        }
    }

//    fun addRecord(record: Record) {
//        dataSet.add(record)
//        notifyDataSetChanged()
//    }
//
//    fun removeRecord(record: Record) {
//        dataSet.remove(record)
//        notifyDataSetChanged()
//    }
}
