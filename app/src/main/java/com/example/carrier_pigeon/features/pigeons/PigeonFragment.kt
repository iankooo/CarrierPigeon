package com.example.carrier_pigeon.features.pigeons

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
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
import com.example.carrier_pigeon.features.pigeons.utils.SwipeToDeleteCallback
import com.example.carrier_pigeon.features.pigeons.utils.SwipeToEditCallback
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PigeonFragment : BaseFragment(R.layout.fragment_pigeon) {
    @Inject
    lateinit var uiThreadPoster: UiThreadPoster

    @Inject
    lateinit var sharedPrefsWrapper: SharedPrefsWrapper

    private val binding by viewBinding(FragmentPigeonBinding::bind)
    private val viewModel by viewModels<PigeonViewModel>()
    private lateinit var pigeonAdapter: PigeonAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.welcomeLabel.text =
            getString(R.string.welcome_comma_first_name_of_user, sharedPrefsWrapper.getFirstName())
        pigeonAdapter =
            PigeonAdapter(
                context?.applicationContext,
                { pigeon -> onPigeonClicked(pigeon) },
                uiThreadPoster
            )
        binding.pigeonsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pigeonAdapter
            setHasFixedSize(true)
        }

        viewModel.allPigeons.observe(
            viewLifecycleOwner
        ) { items ->
            items?.let {
                if (it.isNotEmpty()) {
                    dismissLoading()
                    binding.threeCloudsImageView.invisible()
                    binding.pigeonImageView.invisible()

                    setupEditHandler()
                    setupDeleteHandler()
                } else {
                    binding.threeCloudsImageView.visible()
                    binding.pigeonImageView.visible()
                }

                pigeonAdapter.setItems(it)
            }
        }

        setControls()
    }

    private fun setControls() {
        binding.addButton.setOnClickListener {
            findNavController().navigate(PigeonFragmentDirections.pigeonToAddOrEditPigeon(null))
        }
        binding.pigeonsFlightsBtn.setOnClickListener {
            findNavController().navigate(PigeonFragmentDirections.pigeonToPigeonsFlights())
        }
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(PigeonFragmentDirections.pigeonToProfile())
        }
    }

    private fun setupEditHandler() {
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                findNavController().navigate(
                    PigeonFragmentDirections.pigeonToAddOrEditPigeon(
                        pigeonAdapter.getPigeonFromPosition(
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
                val pigeon = pigeonAdapter.getPigeonFromPosition(viewHolder.adapterPosition)

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.are_you_sure_you_want_to_delete_it))
                builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                    viewModel.delete(pigeon)
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    pigeonAdapter.notifyItemChanged(viewHolder.adapterPosition)
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
