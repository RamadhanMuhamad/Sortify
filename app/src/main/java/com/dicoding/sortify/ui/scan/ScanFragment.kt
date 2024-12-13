package com.dicoding.sortify.ui.scan

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.sortify.R
import com.dicoding.sortify.data.remote.retrofit.ApiConfig
import com.dicoding.sortify.databinding.FragmentScanBinding
import com.dicoding.sortify.helper.getImageUri
import com.dicoding.sortify.helper.reduceFileImage
import com.dicoding.sortify.helper.uriToFile
import com.dicoding.sortify.ui.scan.camera.CameraActivity
import com.dicoding.sortify.ui.scan.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.dicoding.sortify.ui.scan.result.ResultActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.scanButton.setOnClickListener { uploadImage() }


        return binding.root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImage() {
        val uri = currentImageUri
        if (uri != null) {
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                requestImageFile
            )

            showLoading(true)
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val response = apiService.uploadPredict(multipartBody)
                    showToast(response.jsonMemberClass)
                    Log.d(TAG, "uploadImage: ${response.jsonMemberClass}")

                    val intent = Intent(requireContext(), ResultActivity::class.java)
                    intent.putExtra("class", response.jsonMemberClass)
                    intent.putExtra("confidence", response.confidence)
                    // Mengirimkan list rekomendasi sebagai dua List<String>
                    val titles = response.recommendations?.map { it?.title } // List<String>
                    val links = response.recommendations?.map { it?.link }   // List<String>

                    intent.putStringArrayListExtra("recommendation_titles", ArrayList(titles))
                    intent.putStringArrayListExtra("recommendation_links", ArrayList(links))

                    intent.putExtra("imageUri", uri.toString()) // Kirim Uri gambar sebagai String


                    startActivity(intent)

                } catch (e: HttpException) {
                    val errorMessage = e.response()?.errorBody()?.string() ?: getString(R.string.upload_failed)
                    showToast(errorMessage)
                    Log.e(TAG, "uploadImage: ${errorMessage}", )
                } finally {
                    showLoading(false)
                }
            }
        } else {
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
