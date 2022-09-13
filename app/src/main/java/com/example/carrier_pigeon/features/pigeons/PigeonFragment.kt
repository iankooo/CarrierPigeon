package com.example.carrier_pigeon.features.pigeons

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.UiThreadPoster
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.gone
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.data.enums.SharedPrefsWrapper
import com.example.carrier_pigeon.databinding.FragmentPigeonBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PigeonFragment : BaseFragment(R.layout.fragment_pigeon) {
    @Inject
    lateinit var uiThreadPoster: UiThreadPoster

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentPigeonBinding::bind)
    private val viewModel by viewModels<PigeonViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.welcomeLabel.text =
            getString(R.string.welcome_comma_first_name_of_user, sharedPrefsWrapper.getFirstName())
        getPigeonsListFromLocalDB()
        setControls()
    }

    private fun getPigeonsListFromLocalDB() {
        lifecycleScope.launch {
            viewModel.dao.fetchAllPigeons().collect {
                val list = ArrayList(it)
                setupListOfPigeonsIntoRecyclerView(list)
            }
        }
    }

    private fun setControls() {
        binding.addButton.setOnClickListener {
            findNavController().navigate(PigeonFragmentDirections.pigeonToAddOrEditPigeon(null))
        }
        binding.pigeonsFlightsBtn.setOnClickListener {
            findNavController().navigate(PigeonFragmentDirections.pigeonToPigeonsFlights())
        }
    }

    private fun setupListOfPigeonsIntoRecyclerView(
        pigeonsList: ArrayList<Pigeon>
    ) {
        if (pigeonsList.isNotEmpty()) {
            val pigeonAdapter =
                PigeonAdapter(
                    context?.applicationContext,
                    { pigeon -> onPigeonClicked(pigeon) },
                    pigeonsList,
                    uiThreadPoster
                )
            dismissLoading()
            binding.threeCloudsImageView.invisible()
            binding.pigeonImageView.invisible()
            binding.pigeonsRecyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = pigeonAdapter
                setHasFixedSize(true)
            }
            setupEditHandler()
            setupDeleteHandler()
        } else {
            binding.threeCloudsImageView.visible()
            binding.pigeonImageView.visible()
        }
    }

    private fun setupEditHandler() {
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.pigeonsRecyclerview.adapter as PigeonAdapter
                findNavController().navigate(
                    PigeonFragmentDirections.pigeonToAddOrEditPigeon(
                        adapter.getPigeonFromPosition(
                            viewHolder.adapterPosition
                        )
                    )
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.pigeonsRecyclerview)
    }

    private fun setupDeleteHandler() {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.pigeonsRecyclerview.adapter as PigeonAdapter
                val pigeon = adapter.getPigeonFromPosition(viewHolder.adapterPosition)

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.are_you_sure_you_want_to_delete_it))
                builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                    lifecycleScope.launch {
                        viewModel.deletePigeon(pigeon)
                        adapter.removeAt(viewHolder.adapterPosition)
                    }
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                }

                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.pigeonsRecyclerview)
    }

    private fun dismissLoading() {
        binding.loadingView.pauseAnimation()
        binding.loadingView.gone()
    }

    private fun onPigeonClicked(pigeon: Pigeon) {
        findNavController().navigate(
            PigeonFragmentDirections.pigeonToPigeonDetail(
                pigeon
            )
        )
    }
}
