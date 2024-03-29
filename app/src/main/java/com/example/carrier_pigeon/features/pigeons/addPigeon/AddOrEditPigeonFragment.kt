package com.example.carrier_pigeon.features.pigeons.addPigeon

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.Config.FEMALE
import com.example.carrier_pigeon.app.Config.MALE
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.*
import com.example.carrier_pigeon.app.utils.permissions.isPermissionGranted
import com.example.carrier_pigeon.data.enums.CarrierPigeonPermissions
import com.example.carrier_pigeon.databinding.FragmentAddOrEditPigeonBinding
import com.example.carrier_pigeon.features.pigeons.PigeonViewModel
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.example.carrier_pigeon.features.pigeons.detailPigeon.AncestorDescendantBundle
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.util.*

@AndroidEntryPoint
class AddOrEditPigeonFragment : BaseFragment(R.layout.fragment_add_or_edit_pigeon) {
    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "CarrierPigeonImages"
        private const val PIGEON_SELECTED = "pigeon_selected"
    }

    private val viewModel by viewModels<AddOrEditPigeonViewModel>()
    private val pigeonViewModel by viewModels<PigeonViewModel>()
    private val binding by viewBinding(FragmentAddOrEditPigeonBinding::bind)

    private var savePigeonImageToInternalStorage: Uri? = null
    private var savePigeonEyeImageToInternalStorage: Uri? = null
    private var isEyeImageViewClicked = false
    private var pigeon: Pigeon? = null
    private var ancestorDescendantMotherBundle: AncestorDescendantBundle? = null
    private var ancestorDescendantFatherBundle: AncestorDescendantBundle? = null

    private var requestPermissionsLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allAreGranted = true
            for (value in result.values) {
                allAreGranted = allAreGranted && value
            }

            if (allAreGranted) {
                viewModel.havePermissionsBeenPreviouslyDenied = false
            } else {
                if (shouldShowRequestPermissionRationale(CarrierPigeonPermissions.CAMERA.type) ||
                    shouldShowRequestPermissionRationale(CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type)
                ) {
                    showRationaleDialog(
                        CarrierPigeonPermissions.getPermissions(),
                        getString(R.string.permission_rationale_dialog_title),
                        getString(R.string.permission_rationale_dialog_message)
                    )
                }

                when (isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type)) {
                    true -> viewModel.havePermissionsBeenPreviouslyDenied = false
                    false -> viewModel.havePermissionsBeenPreviouslyDenied = true
                }

                if (!isPermissionGranted(
                        requireContext(),
                        CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                    )
                ) {
                    viewModel.havePermissionsBeenPreviouslyDenied = true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pigeon = arguments?.getParcelable(PIGEON_SELECTED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (pigeon != null) {
            setupEditView()
        } else {
            setupAddView()
        }

        setControls()
    }

    private fun setupAddView() {
        binding.headerTitle.welcomeLabel.setText(R.string.add_new_pigeon)
        binding.savePigeonBtn.setText(R.string.save_pigeon)
    }

    private fun setupEditView() {
        binding.headerTitle.welcomeLabel.setText(R.string.edit_pigeon)
        binding.savePigeonBtn.setText(R.string.save_changes)

        with(pigeon) {
            binding.pigeonGender.isActivated = this!!.gender == MALE
            binding.mainRl.isActivated = gender == MALE
            binding.pigeonCountry.setDefaultCountryUsingNameCode(country)
            binding.pigeonSeries.setText(series)
            binding.pigeonNickname.setText(nickname)
            binding.pigeonColor.setText(color)
            binding.pigeonDetails.setText(details)
            if (!pigeonImage.isNullOrEmpty()) {
                savePigeonImageToInternalStorage = Uri.parse(pigeonImage)
                binding.addPigeonImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.addPigeonImageView.setImageURI(Uri.parse(pigeonImage))
            }
            if (!pigeonEyeImage.isNullOrEmpty()) {
                savePigeonEyeImageToInternalStorage = Uri.parse(pigeonEyeImage)
                binding.addPigeonEyeImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.addPigeonEyeImageView.setImageURI(Uri.parse(pigeonEyeImage))
            }
            binding.pigeonDateOfBirth.setText(dateOfBirth)
            binding.firstVaccine.isChecked = firstVaccine == 1
            if (firstVaccine == 1)
                binding.secondVaccine.visible()
            binding.secondVaccine.isChecked = secondVaccine == 1
            if (secondVaccine == 1)
                binding.thirdVaccine.visible()
            binding.thirdVaccine.isChecked = thirdVaccine == 1

            if (motherPigeon != null) {
                val mother = pigeonViewModel.getFamilyMemberById(motherPigeon!!)
                if (mother != null) {
                    binding.parents.motherTv.text = resources.getString(
                        R.string.two_strings_format,
                        mother.country,
                        mother.series
                    )
                    if (mother.pigeonImage.isNullOrEmpty()) {
                        binding.parents.motherImage.setBackgroundResource(R.drawable.ic_class_image_placeholder)
                    } else {
                        binding.parents.motherImage.setImageURI(Uri.parse(mother.pigeonImage))
                    }
                }
            }
            if (fatherPigeon != null) {
                val father = pigeonViewModel.getFamilyMemberById(fatherPigeon!!)
                if (father != null) {
                    binding.parents.fatherTv.text = resources.getString(
                        R.string.two_strings_format,
                        father.country,
                        father.series
                    )
                    if (father.pigeonImage.isNullOrEmpty()) {
                        binding.parents.fatherImage.setBackgroundResource(R.drawable.ic_class_image_placeholder)
                    } else {
                        binding.parents.fatherImage.setImageURI(Uri.parse(father.pigeonImage))
                    }
                }
            }
        }
    }

    private fun setControls() {
        binding.headerTitle.addButton.invisible()
        binding.headerTitle.profileBtn.invisible()
        binding.headerTitle.backButton.visible()
        binding.headerTitle.exportToPdfBtn.invisible()

        binding.headerTitle.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        if (pigeon == null) {
            binding.pigeonGender.setOnClickListener {
                binding.pigeonGender.isActivated = !binding.pigeonGender.isActivated
                binding.mainRl.isActivated = !binding.mainRl.isActivated
            }
        }

        binding.pigeonDateOfBirth.transformIntoDatePicker(requireContext(), DATE_FORMAT)

        binding.addPigeonImageView.setOnClickListener {
            isEyeImageViewClicked = false
            setupPictureDialog(getString(R.string.pigeon_photo))
        }
        binding.addPigeonEyeImageView.setOnClickListener {
            isEyeImageViewClicked = true
            setupPictureDialog(getString(R.string.pigeon_eye_photo))
        }

        binding.firstVaccine.setOnClickListener {
            if (binding.firstVaccine.isChecked) {
                binding.secondVaccine.isChecked = false
                binding.thirdVaccine.isChecked = false
                binding.secondVaccine.visible()
            } else {
                binding.secondVaccine.invisible()
                binding.thirdVaccine.invisible()
            }
        }

        binding.secondVaccine.setOnClickListener {
            if (binding.secondVaccine.isChecked) {
                binding.thirdVaccine.isChecked = false
                binding.thirdVaccine.visible()
            } else {
                binding.thirdVaccine.invisible()
            }
        }

        binding.savePigeonBtn.setOnClickListener {
            savePigeon()
        }

        binding.parents.motherView.setOnClickListener {
            addParent(R.drawable.ic_female, "Choose mother", FEMALE)
        }

        binding.parents.fatherView.setOnClickListener {
            addParent(R.drawable.ic_male, "Choose father", MALE)
        }
    }

    private fun addParent(
        @DrawableRes parentDrawable: Int,
        alertDialogTitle: CharSequence,
        parentGender: String
    ) {
        val builderMultiple: AlertDialog.Builder =
            AlertDialog.Builder(requireContext())
        builderMultiple.setIcon(parentDrawable)
        builderMultiple.setTitle(alertDialogTitle)
        var ancestorPigeon: Pigeon? = null

        pigeonViewModel.allPigeons.observe(
            viewLifecycleOwner
        ) {
            val availablePigeonsList =
                it.filter { it2 -> it2.gender == parentGender && it2 != pigeon }

            if (availablePigeonsList.isNotEmpty()) {
                val items = arrayOfNulls<String>(availablePigeonsList.size)

                for (i in items.indices) {
                    items[i] =
                        availablePigeonsList[i].country + " " + availablePigeonsList[i].series
                }

                builderMultiple.setSingleChoiceItems(
                    items, -1
                ) { _, which ->
                    ancestorPigeon = availablePigeonsList[which]
                }

                builderMultiple.setPositiveButton(
                    getString(R.string.ok)
                ) { _, _ ->
                    if (ancestorPigeon != null) {
                        if (parentGender == FEMALE) {
                            binding.parents.motherTv.text = resources.getString(
                                R.string.two_strings_format,
                                ancestorPigeon?.country,
                                ancestorPigeon?.series
                            )
                            if (!ancestorPigeon?.pigeonImage.isNullOrEmpty())
                                binding.parents.motherImage.setImageURI(Uri.parse(ancestorPigeon?.pigeonImage))

                            ancestorDescendantMotherBundle =
                                AncestorDescendantBundle(ancestorPigeon, pigeon, 1)
                        } else {
                            binding.parents.fatherTv.text = resources.getString(
                                R.string.two_strings_format,
                                ancestorPigeon?.country,
                                ancestorPigeon?.series
                            )
                            if (!ancestorPigeon?.pigeonImage.isNullOrEmpty())
                                binding.parents.fatherImage.setImageURI(Uri.parse(ancestorPigeon?.pigeonImage))
                            ancestorDescendantFatherBundle =
                                AncestorDescendantBundle(ancestorPigeon, pigeon, 1)
                        }
                    }
                }
            } else {
                builderMultiple.setMessage(getString(R.string.no_pigeons_available))
            }
            builderMultiple.show()
        }
    }

    private fun setupPictureDialog(path: String) {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle(path)
        val pictureDialogItems =
            arrayOf(
                getString(R.string.select_photo_from_gallery),
                getString(R.string.capture_photo_from_camera)
            )
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun savePigeon() {
        val gender = if (binding.pigeonGender.isActivated) MALE else FEMALE
        val country = binding.pigeonCountry.selectedCountryNameCode.toString()
        val series = binding.pigeonSeries.text.toString()
        val nickname = binding.pigeonNickname.text.toString()
        val color = binding.pigeonColor.text.toString()
        val details = binding.pigeonDetails.text.toString()
        val pigeonImage: String? = if (savePigeonImageToInternalStorage == null) {
            null
        } else {
            savePigeonImageToInternalStorage.toString()
        }
        val pigeonEyeImage: String? = if (savePigeonEyeImageToInternalStorage == null) {
            null
        } else {
            savePigeonEyeImageToInternalStorage.toString()
        }
        val dateOfBirth = binding.pigeonDateOfBirth.text.toString()
        val firstVaccine = if (binding.firstVaccine.isChecked) 1 else 0
        val secondVaccine = if (binding.secondVaccine.isChecked) 1 else 0
        val thirdVaccine = if (binding.thirdVaccine.isChecked) 1 else 0
        val mother = pigeon?.motherPigeon
        val father = pigeon?.fatherPigeon

        if (series.isEmpty())
            shortToast(getString(R.string.series_cannot_be_empty))
        else if (nickname.isEmpty())
            shortToast(getString(R.string.nickname_cannot_be_empty))
        else if (color.isEmpty())
            shortToast(getString(R.string.color_cannot_be_empty))
        else {
            val pigeon = Pigeon(
                series = series,
                gender = gender,
                country = country,
                nickname = nickname,
                color = color,
                details = details,
                pigeonImage = pigeonImage,
                pigeonEyeImage = pigeonEyeImage,
                dateOfBirth = dateOfBirth,
                firstVaccine = firstVaccine,
                secondVaccine = secondVaccine,
                thirdVaccine = thirdVaccine,
                familyTreeId = 0,
                motherPigeon = mother,
                fatherPigeon = father
            )
            if (this.pigeon != null) {
                updatePigeon(pigeon)
            } else {
                insertPigeon(pigeon)
            }
            findNavController().popBackStack()
        }
    }

    private fun insertPigeon(pigeon: Pigeon) {
        val job = pigeonViewModel.insert(pigeon)

        if (job.isCompleted) {
            val foundPigeon =
                pigeonViewModel.getPigeonBySeriesAndGender(pigeon.series, pigeon.gender)

            if (ancestorDescendantFatherBundle != null)
                foundPigeon.fatherPigeon = ancestorDescendantFatherBundle?.newPigeon?.id
            if (ancestorDescendantMotherBundle != null)
                foundPigeon.motherPigeon = ancestorDescendantMotherBundle?.newPigeon?.id
            pigeonViewModel.update(foundPigeon)

            if (ancestorDescendantFatherBundle != null) {
                ancestorDescendantFatherBundle!!.existingPigeon = foundPigeon
                pigeonViewModel.insertAncestor(ancestorDescendantFatherBundle!!)
            }
            if (ancestorDescendantMotherBundle != null) {
                ancestorDescendantMotherBundle!!.existingPigeon = foundPigeon
                pigeonViewModel.insertAncestor(ancestorDescendantMotherBundle!!)
            }
        }
    }

    private fun updatePigeon(pigeon: Pigeon) {
        pigeon.id = this.pigeon!!.id
        if (ancestorDescendantFatherBundle != null)
            pigeon.fatherPigeon = ancestorDescendantFatherBundle?.newPigeon?.id
        if (ancestorDescendantMotherBundle != null)
            pigeon.motherPigeon = ancestorDescendantMotherBundle?.newPigeon?.id
        pigeonViewModel.update(pigeon)

        if (ancestorDescendantFatherBundle != null) {
            pigeonViewModel.insertAncestor(ancestorDescendantFatherBundle!!)
        }
        if (ancestorDescendantMotherBundle != null) {
            pigeonViewModel.insertAncestor(ancestorDescendantMotherBundle!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type) &&
            isPermissionGranted(
                    requireContext(),
                    CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                )
        ) {
            if (viewModel.havePermissionsBeenPreviouslyDenied) {
                viewModel.havePermissionsBeenPreviouslyDenied = false
            }
        }
    }

    private fun takePhotoFromCamera() {
        when {
            isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type) &&
                isPermissionGranted(
                    requireContext(),
                    CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                ) -> {
                viewModel.havePermissionsBeenPreviouslyDenied = false
                val galleryIntent =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(galleryIntent, CAMERA)
            }
            isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type).not() ||
                isPermissionGranted(
                    requireContext(),
                    CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                ).not() -> {
                if (shouldShowRequestPermissionRationale(CarrierPigeonPermissions.CAMERA.type) ||
                    shouldShowRequestPermissionRationale(CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type)
                ) {
                    showRationaleDialog(
                        CarrierPigeonPermissions.getPermissions(),
                        getString(R.string.permission_rationale_dialog_title),
                        getString(R.string.permission_rationale_dialog_message)
                    )
                } else
                    requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
            else -> {
                requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
        }
    }

    private fun choosePhotoFromGallery() {
        when {
            isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type) &&
                isPermissionGranted(
                    requireContext(),
                    CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                ) -> {
                viewModel.havePermissionsBeenPreviouslyDenied = false
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY)
            }
            isPermissionGranted(requireContext(), CarrierPigeonPermissions.CAMERA.type).not() ||
                isPermissionGranted(
                    requireContext(),
                    CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type
                ).not() -> {
                if (shouldShowRequestPermissionRationale(CarrierPigeonPermissions.CAMERA.type) ||
                    shouldShowRequestPermissionRationale(CarrierPigeonPermissions.READ_EXTERNAL_STORAGE.type)
                ) {
                    showRationaleDialog(
                        CarrierPigeonPermissions.getPermissions(),
                        getString(R.string.permission_rationale_dialog_title),
                        getString(R.string.permission_rationale_dialog_message)
                    )
                } else
                    requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
            else -> {
                requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                        if (isEyeImageViewClicked) {
                            savePigeonEyeImageToInternalStorage =
                                saveImageToInternalStorage(selectedImageBitmap)
                            binding.addPigeonEyeImageView.scaleType =
                                ImageView.ScaleType.CENTER_CROP
                            binding.addPigeonEyeImageView.setImageBitmap(selectedImageBitmap)
                        } else {
                            savePigeonImageToInternalStorage =
                                saveImageToInternalStorage(selectedImageBitmap)
                            binding.addPigeonImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                            binding.addPigeonImageView.setImageBitmap(selectedImageBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                if (isEyeImageViewClicked) {
                    savePigeonEyeImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                    binding.addPigeonEyeImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.addPigeonEyeImageView.setImageBitmap(thumbnail)
                } else {
                    savePigeonImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                    binding.addPigeonImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.addPigeonImageView.setImageBitmap(thumbnail)
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(context?.applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    override fun onAllowClicked(permissions: Array<String>) {
        super.onAllowClicked(permissions)
        requestPermissionsLauncher.launch(permissions)
    }

    override fun onDenyClicked(permissions: Array<String>) {
        super.onDenyClicked(permissions)
        viewModel.havePermissionsBeenPreviouslyDenied = true
    }
}
