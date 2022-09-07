package com.example.carrier_pigeon.features.main.addPigeon

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.common.BaseFragment
import com.example.carrier_pigeon.app.utils.permissions.isPermissionGranted
import com.example.carrier_pigeon.app.utils.shortToast
import com.example.carrier_pigeon.data.enums.CarrierPigeonPermissions
import com.example.carrier_pigeon.databinding.FragmentAddPigeonBinding
import com.example.carrier_pigeon.features.main.data.Pigeon
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

@AndroidEntryPoint
class AddPigeonFragment : BaseFragment(R.layout.fragment_add_pigeon) {
    private val viewModel by viewModels<AddPigeonViewModel>()
    private val binding by viewBinding(FragmentAddPigeonBinding::bind)
    private var savePigeonImageToInternalStorage: Uri? = null
    private var savePigeonEyeImageToInternalStorage: Uri? = null
    private var isEyeImageViewClicked = false
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

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "CarrierPigeonImages"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pigeonGender.setOnClickListener {
            binding.pigeonGender.isActivated = !binding.pigeonGender.isActivated
            binding.mainRl.isActivated = !binding.mainRl.isActivated
        }

        binding.savePigeonBtn.setOnClickListener {
            addPigeon()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
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

    private fun addPigeon() {
        val gender: String = if (binding.pigeonGender.isActivated) {
            com.example.carrier_pigeon.app.Config.MALE
        } else {
            com.example.carrier_pigeon.app.Config.FEMALE
        }
        val country = binding.ccp.selectedCountryNameCode.toString()
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

        if (series.isNotEmpty() && country.isNotEmpty() && color.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.addPigeon(
                    Pigeon(
                        series,
                        gender,
                        country,
                        nickname,
                        color,
                        details,
                        pigeonImage,
                        pigeonEyeImage
                    )
                )
                findNavController().popBackStack()
            }
        } else {
            if (series.isEmpty())
                shortToast(getString(R.string.series_cannot_be_empty))
            else if (nickname.isEmpty())
                shortToast(getString(R.string.nickname_cannot_be_empty))
            else if (color.isEmpty())
                shortToast(getString(R.string.color_cannot_be_empty))
        }
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
