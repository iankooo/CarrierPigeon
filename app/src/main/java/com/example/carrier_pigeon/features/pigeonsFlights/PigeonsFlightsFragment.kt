package com.example.carrier_pigeon.features.pigeonsFlights

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.shortToast
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentPigeonsFlightsBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PigeonsFlightsFragment :
    BaseFragment(R.layout.fragment_pigeons_flights) {
    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

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
        footer[1].setOnClickListener {
            deletePigeonFromListView()
        }

        binding.exportToPdfBtn.setOnClickListener {
            createPdf()
        }
    }

    private fun deletePigeonFromListView() {
        val builderMultiple: AlertDialog.Builder =
            AlertDialog.Builder(requireContext())
        builderMultiple.setIcon(R.drawable.ic_pigeons)
        builderMultiple.setTitle(getString(R.string.delete_pigeons_from_flight))

        val pigeonsRecorded = recordAdapter.getAllRecords()

        if (pigeonsRecorded.isNotEmpty()) {
            val items = arrayOfNulls<String>(pigeonsRecorded.size)
            val checkedItems = BooleanArray(pigeonsRecorded.size)

            for (i in items.indices) {
                checkedItems[i] = true
                items[i] =
                    pigeonsRecorded[i].country + " " + pigeonsRecorded[i].series
            }

            val selectedPigeons = ArrayList<Record>()

            builderMultiple.setMultiChoiceItems(
                items, checkedItems
            ) { dialog, which, isChecked ->
                if (isChecked) {
                    selectedPigeons.remove(pigeonsRecorded[which])
                } else {
                    selectedPigeons.add(pigeonsRecorded[which])
                }
            }

            builderMultiple.setPositiveButton(
                getString(R.string.ok)
            ) { dialog, which ->
                recordAdapter.notifyDataSetChanged()
                viewModel.deleteAllRecords(selectedPigeons)
            }
        } else {
            builderMultiple.setMessage(getString(R.string.no_pigeons_available))
        }
        builderMultiple.show()
    }

    private fun addPigeonToListView() {
        val builderMultiple: AlertDialog.Builder =
            AlertDialog.Builder(requireContext())
        builderMultiple.setIcon(R.drawable.ic_pigeons)
        builderMultiple.setTitle(getString(R.string.add_pigeons_to_flight))

        pigeonViewModel.allPigeons.observe(
            viewLifecycleOwner
        ) {
            val pigeonsList = convertListOfPigeonToListOfRecord(it)
            val pigeonsRecorded = recordAdapter.getAllRecords()
            val listsAdded = pigeonsList + pigeonsRecorded
            val availablePigeonsList =
                listsAdded.groupBy { it2 -> it2.series }.filter { it2 -> it2.value.size == 1 }
                    .flatMap { it2 -> it2.value }

            if (availablePigeonsList.isNotEmpty()) {
                val items = arrayOfNulls<String>(availablePigeonsList.size)
                val checkedItems = BooleanArray(availablePigeonsList.size)

                for (i in items.indices) {
                    items[i] =
                        availablePigeonsList[i].country + " " + availablePigeonsList[i].series
                }

                val selectedPigeons = ArrayList<Record>()

                builderMultiple.setMultiChoiceItems(
                    items, checkedItems
                ) { dialog, which, isChecked ->
                    if (isChecked) {
                        selectedPigeons.add(availablePigeonsList[which])
                    } else {
                        selectedPigeons.remove(availablePigeonsList[which])
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

    private fun convertListOfPigeonToListOfRecord(pigeonsList: List<Pigeon>): List<Record> {
        return pigeonsList.map {
            with(it) {
                Record(
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
    }

    private fun createPdf() {
        val onError: (Exception) -> Unit = { toastErrorMessage(it.message.toString()) }
        val onFinish: (File) -> Unit = { openFile(it) }
        val paragraphList = listOf(
            getString(
                R.string.fancier,
                sharedPrefsWrapper.getLastName(),
                sharedPrefsWrapper.getFirstName()
            ),
            getString(
                R.string.phone_number,
                sharedPrefsWrapper.getPhoneNumber()
            ),
            getString(
                R.string.home_address,
                sharedPrefsWrapper.getHomeAddress()
            )
        )
        val pdfService = PdfService(requireContext())
        pdfService.createUserTable(
            data = recordAdapter.getAllRecords(),
            paragraphList = paragraphList,
            onFinish = onFinish,
            onError = onError
        )
    }

    private fun openFile(file: File) {
        val path = file.toUri().path
        val pdfFile = File(path.toString())
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfFile.toUri(), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            toastErrorMessage("Can't read pdf file")
        }
    }

    private fun toastErrorMessage(s: String) {
        shortToast(s)
    }
}
