package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentPigeonsFlightsBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

class PigeonsFlightsFragment :
    BaseFragment(R.layout.fragment_pigeons_flights) {
    private val binding by viewBinding(FragmentPigeonsFlightsBinding::bind)
    private val viewModel by viewModels<PigeonsFlightsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        val description = arrayListOf(
            Record("001", "RO", "20", "883275", "F", "GUT", "X X X")
        )
        val recordAdapter = RecordAdapter(description, context?.applicationContext)
        val footer: ViewGroup =
            layoutInflater.inflate(
                R.layout.item_footer_pigeons_flights,
                binding.recordsView,
                false
            ) as ViewGroup
        binding.recordsView.addFooterView(footer)
        binding.recordsView.adapter = recordAdapter
        footer[0].setOnClickListener {
            addPigeonToListView(recordAdapter)
        }
    }

    private fun addPigeonToListView(recordAdapter: RecordAdapter) {
        val builderMultiple: AlertDialog.Builder =
            AlertDialog.Builder(requireContext())
        builderMultiple.setIcon(R.drawable.ic_pigeons)
        builderMultiple.setTitle(getString(R.string.select_pigeons_for_flight))

        lifecycleScope.launch {
            viewModel.dao.fetchAllPigeons().collect {
                val pigeonsList = convertListOfPigeonToListOfRecord(it)
                val pigeonsRecorded = recordAdapter.getAllRecords()
                if (pigeonsList.isNotEmpty()) {
                    val items = arrayOfNulls<String>(pigeonsList.size)
                    val checkedItems = BooleanArray(pigeonsList.size)
                    for (i in items.indices) {
                        checkedItems[i] = pigeonsRecorded.contains(pigeonsList[i].series)
                        items[i] = pigeonsList[i].country + " " + pigeonsList[i].series
                    }
                    val selectedPigeons = ArrayList<Record>()
                    val removedPigeons = ArrayList<Record>()

                    builderMultiple.setMultiChoiceItems(
                        items, checkedItems
                    ) { dialog, which, isChecked ->
                        if (isChecked) {
                            selectedPigeons.add(pigeonsList[which])
                            removedPigeons.remove(pigeonsList[which])
                        } else {
                            removedPigeons.add(pigeonsList[which])
                            selectedPigeons.remove(pigeonsList[which])
                        }
                    }

                    builderMultiple.setPositiveButton(
                        getString(R.string.ok)
                    ) { dialog, which ->
                        recordAdapter.addRecords(selectedPigeons)
                        recordAdapter.removeRecords(removedPigeons)
                    }
                    builderMultiple.setNegativeButton(
                        getString(R.string.cancel)
                    ) { dialog, which -> dialog.dismiss() }
                } else {
                    builderMultiple.setMessage(getString(R.string.no_pigeons_available))
                }
                builderMultiple.show()
            }
        }
    }

    private fun convertListOfPigeonToListOfRecord(list: List<Pigeon>): List<Record> {
        val pigeonsList = list.map {
            with(it) {
                Record(
                    id.toString(),
                    country,
                    " ",
                    series,
                    gender,
                    color,
                    ""
                )
            }
        }
        return pigeonsList
    }
}
