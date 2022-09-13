package com.example.carrier_pigeon.features.pigeons.addPigeon

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.Config
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.invisible
import com.example.carrier_pigeon.app.utils.permissions.isPermissionGranted
import com.example.carrier_pigeon.app.utils.shortToast
import com.example.carrier_pigeon.app.utils.visible
import com.example.carrier_pigeon.data.enums.CarrierPigeonPermissions
import com.example.carrier_pigeon.databinding.FragmentAddOrEditPigeonBinding
import com.example.carrier_pigeon.features.pigeons.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
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
    private val binding by viewBinding(FragmentAddOrEditPigeonBinding::bind)
    private var savePigeonImageToInternalStorage: Uri? = null
    private var savePigeonEyeImageToInternalStorage: Uri? = null
    private var isEyeImageViewClicked = false
    private var pigeon: Pigeon? = null
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
            binding.welcomeLabel.setText(R.string.edit_pigeon)
            binding.savePigeonBtn.setText(R.string.save_changes)

            with(pigeon) {
                binding.pigeonGender.isActivated = this!!.gender == Config.MALE
                binding.mainRl.isActivated = gender == Config.MALE
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
            }
        } else {
            binding.welcomeLabel.setText(R.string.add_new_pigeon)
            binding.savePigeonBtn.setText(R.string.save_pigeon)
        }

        binding.pigeonGender.setOnClickListener {
            binding.pigeonGender.isActivated = !binding.pigeonGender.isActivated
            binding.mainRl.isActivated = !binding.mainRl.isActivated
        }

        binding.firstVaccine.setOnClickListener {
            if (binding.firstVaccine.isChecked)
                binding.secondVaccine.visible()
            else
                binding.secondVaccine.invisible()
        }

        binding.secondVaccine.setOnClickListener {
            if (binding.secondVaccine.isChecked)
                binding.thirdVaccine.visible()
            else {
                binding.secondVaccine.invisible()
                binding.thirdVaccine.invisible()
            }
        }
        binding.thirdVaccine.setOnClickListener {
            if (binding.thirdVaccine.isChecked)
                binding.thirdVaccine.visible()
            else
                binding.thirdVaccine.invisible()
        }

        binding.savePigeonBtn.setOnClickListener {
            savePigeon()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pigeonDateOfBirth.transformIntoDatePicker(requireContext(), "MMM/dd/yyyy")

        binding.addPigeonImageView.setOnClickListener {
            isEyeImageViewClicked = false
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Pigeon photo")
            val pictureDialogItems =
                arrayOf("Select photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
        binding.addPigeonEyeImageView.setOnClickListener {
            isEyeImageViewClicked = true
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Pigeon eye photo")
            val pictureDialogItems =
                arrayOf("Select photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
    }

    private fun savePigeon() {
        val gender: String = if (binding.pigeonGender.isActivated) {
            Config.MALE
        } else {
            Config.FEMALE
        }
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
        val firstVaccine: Int = if (binding.firstVaccine.isChecked) 1 else 0
        val secondVaccine: Int = if (binding.secondVaccine.isChecked) 1 else 0
        val thirdVaccine: Int = if (binding.thirdVaccine.isChecked) 1 else 0

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
                thirdVaccine = thirdVaccine
            )
            if (this.pigeon != null) {
                pigeon.id = this.pigeon!!.id
                lifecycleScope.launch {
                    viewModel.editPigeon(pigeon)
                }
            } else {
                lifecycleScope.launch {
                    viewModel.addPigeon(pigeon)
                }
            }
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        // Handle return in the app from settings only when the user has previously denied the permissions
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
                // Asking for permissions directly
                    requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
            else -> {
                // Asking for permissions directly
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
                // Asking for permissions directly
                    requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
            else -> {
                // Asking for permissions directly
                requestPermissionsLauncher.launch(CarrierPigeonPermissions.getPermissions())
            }
        }
    }

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

    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, R.style.CustomDatePickerDialogTheme, datePickerOnDataSetListener,
                myCalendar
                    .get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                datePicker.maxDate = System.currentTimeMillis()
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }
}
