package com.example.eventconnect.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerHelper(private val fragment: Fragment) {

    private var onImageSelected: ((Uri?) -> Unit)? = null

    private val galleryLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected?.invoke(uri)
    }

    private val cameraLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImageSelected?.invoke(currentPhotoUri)
        }
    }

    private var currentPhotoUri: Uri? = null

    fun pickImageFromGallery(onImageSelected: (Uri?) -> Unit) {
        this.onImageSelected = onImageSelected
        galleryLauncher.launch("image/*")
    }

    fun takePhotoWithCamera(onImageSelected: (Uri?) -> Unit) {
        this.onImageSelected = onImageSelected
        currentPhotoUri = createImageUri(fragment.requireContext())
        currentPhotoUri?.let {
            cameraLauncher.launch(it)
        }
    }

    /**
     * Crée un fichier temporaire et retourne un Uri via FileProvider
     */
    private fun createImageUri(context: Context): Uri? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // attention: doit matcher manifest
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
