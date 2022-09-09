package com.example.carrier_pigeon.features.pigeonsFlights

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentPigeonsFlightsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class PigeonsFlightsFragment :
    BaseFragment(com.example.carrier_pigeon.R.layout.fragment_pigeons_flights) {
    private val binding by viewBinding(FragmentPigeonsFlightsBinding::bind)
    private var recordAdapter: RecordAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        val recordsView: ListView = binding.recordsView
        recordsView.adapter = recordAdapter
        val list: MutableList<Record> =
            mutableListOf(
                Record("1", "RO", "2022", "12345", "F", "GUT", "YES"),
                Record("1", "RO", "2022", "12345", "F", "GUT", "YES"),
                Record("1", "RO", "2022", "12345", "F", "GUT", "YES"),
                Record("1", "RO", "2022", "12345", "F", "GUT", "YES"),
                Record("1", "RO", "2022", "12345", "F", "GUT", "YES")
            )
        recordAdapter = RecordAdapter(context, list)
    }
}
