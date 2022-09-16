package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.databinding.FragmentPigeonsFlightsBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PigeonsFlightsFragment :
    BaseFragment(R.layout.fragment_pigeons_flights) {
    private val binding by viewBinding(FragmentPigeonsFlightsBinding::bind)
    private val viewModel by viewModels<PigeonsFlightsViewModel>()
    private val pigeonViewModel by viewModels<PigeonViewModel>()

    private lateinit var recordAdapter: RecordAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.allRecords.observe(
            viewLifecycleOwner
        ) { items ->
            items?.let {
                recordAdapter = RecordAdapter(ArrayList(it), context)
                recordAdapter.setItems(it)
                binding.recordsListView.adapter = recordAdapter
            }
        }

        val footer: ViewGroup =
            layoutInflater.inflate(
                R.layout.item_footer_pigeons_flights,
                binding.recordsListView,
                false
            ) as ViewGroup

        binding.recordsListView.addFooterView(footer)

        footer[0].setOnClickListener {
            addPigeonToListView()
        }
    }

    private fun addPigeonToListView() {
        val builderMultiple: AlertDialog.Builder =
            AlertDialog.Builder(requireContext())
        builderMultiple.setIcon(R.drawable.ic_pigeons)
        builderMultiple.setTitle(getString(R.string.select_pigeons_for_flight))

        pigeonViewModel.allPigeons.observe(
            viewLifecycleOwner
        ) {
            val pigeonsList = convertListOfPigeonToListOfRecord(it)
            val pigeonsRecorded = recordAdapter.getAllRecords()
            val sum = pigeonsList + pigeonsRecorded
            val officialList =
                sum.groupBy { it2 -> it2.series }.filter { it2 -> it2.value.size == 1 }
                    .flatMap { it2 -> it2.value }

            if (officialList.isNotEmpty()) {
                val items = arrayOfNulls<String>(officialList.size)
                val checkedItems = BooleanArray(officialList.size)
                for (i in items.indices) {
                    items[i] = officialList[i].country + " " + officialList[i].series
                }
                val selectedPigeons = ArrayList<Record>()

                builderMultiple.setMultiChoiceItems(
                    items, checkedItems
                ) { dialog, which, isChecked ->
                    if (isChecked) {
                        officialList[which].nr = pigeonsRecorded.size + selectedPigeons.size + 1
                        selectedPigeons.add(officialList[which])
                    } else {
                        selectedPigeons.remove(officialList[which])
                    }
                }

                builderMultiple.setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, which ->
                    recordAdapter.notifyDataSetChanged()
                    viewModel.insertAllRecord(selectedPigeons)
                }
            } else {
                builderMultiple.setMessage(getString(R.string.no_pigeons_available))
            }
            builderMultiple.show()
        }
    }

    private fun convertListOfPigeonToListOfRecord(list: List<Pigeon>): List<Record> {
        val pigeonsList = list.map {
            with(it) {
                Record(
                    nr = id,
                    country = country,
                    dateOfBirth = dateOfBirth.toString(),
                    series = series,
                    gender = gender,
                    color = color,
                    firstVaccine = firstVaccine,
                    secondVaccine = secondVaccine,
                    thirdVaccine = thirdVaccine
                )
            }
        }
        return pigeonsList
    }
}
