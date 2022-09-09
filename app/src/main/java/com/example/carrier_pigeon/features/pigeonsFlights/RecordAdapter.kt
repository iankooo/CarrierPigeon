package com.example.carrier_pigeon.features.pigeonsFlights

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RecordAdapter(context: Context?, val recordList: MutableList<Record>) : BaseAdapter() {
    private var recordContext: Context? = null

    override fun getCount(): Int {
        return recordList.size
    }

    override fun getItem(position: Int): Any {
        return recordList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View, parent: ViewGroup?): View {

        val holder: RecordViewHolder = view.tag as RecordViewHolder

        val record = getItem(position) as Record
        holder.nrView?.text = record.nr
        holder.countryView?.text = record.country
        holder.dateOfBirthView?.text = record.dateOfBirth
        holder.seriesView?.text = record.series
        holder.genderView?.text = record.gender
        holder.colorView?.text = record.color
        holder.vaccineView?.text = record.vaccine
        return view
    }

    fun add(record: Record) {
        recordList.add(record)
        notifyDataSetChanged()
    }

    private class RecordViewHolder {
        var nrView: TextView? = null
        var countryView: TextView? = null
        var dateOfBirthView: TextView? = null
        var seriesView: TextView? = null
        var genderView: TextView? = null
        var colorView: TextView? = null
        var vaccineView: TextView? = null
    }
}
