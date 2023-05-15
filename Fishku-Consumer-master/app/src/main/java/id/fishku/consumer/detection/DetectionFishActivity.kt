package id.fishku.consumer.detection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.utils.reduceFileImage
import id.fishku.consumer.core.utils.rotateBitmap
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.core.utils.uriToFile
import id.fishku.consumer.databinding.ActivityDetectionFishBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class DetectionFishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetectionFishBinding

    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private val detectionViewModel: DetectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionFishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermission()
        setupAction()
        detectionResult()
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                "Tidak mendapatkan akses.".showMessage(this)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAction() {
        binding.apply {
            toolbarDetection.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnGallery.setOnClickListener(this@DetectionFishActivity)
            btnCamera.setOnClickListener(this@DetectionFishActivity)
            btnDetection.setOnClickListener(this@DetectionFishActivity)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile
            binding.imgPhotoInput.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                true
            )

            binding.imgPhotoInput.setImageBitmap(result)
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.resolveActivity(packageManager)
        }

        id.fishku.consumer.core.utils.createTempFile(this).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "id.fishku.consumer",
                it
            )
            currentPhotoPath = it.absolutePath

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun detectionResult() {
        detectionViewModel.detectionResult.observe(this) {
            when (it) {
                is Resource.Loading -> true.showLoading()
                is Resource.Success -> {
                    false.showLoading()
                    it.data?.let { data ->
                        val result = data.prediction
                        val resultContext = "Hasil gambar menunjukkan ikan tergolong $result"
                        if (result == "Segar") {
                            showDetectionDialog(
                                "Segar",
                                resultContext,
                                true
                            )
                        } else {
                            showDetectionDialog(
                                "Tidak Segar",
                                resultContext,
                                false
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    false.showLoading()
                    it.message?.showMessage(this)
                }
            }
        }
    }

    private fun Boolean.showLoading() {
        if (this) {
            binding.apply {
                loadingDetection.visibility = View.VISIBLE
                btnCamera.isClickable = false
                btnGallery.isClickable = false
                btnDetection.isClickable = false
            }
        } else {
            binding.apply {
                loadingDetection.visibility = View.GONE
                btnCamera.isClickable = true
                btnGallery.isClickable = true
                btnDetection.isClickable = true
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file", // Nama Key di Request Params API
                file.name,
                requestImageFile
            )
            val fishName = DetectionFishActivityArgs.fromBundle(intent.extras as Bundle).fishName
//            val fishPart = DetectionFishActivityArgs.fromBundle(requireArguments()).fishPart  // Soon for fish part

            detectionViewModel.uploadImageDetection(fishName, imageMultipart)
        } else {
            getString(R.string.photo_cannot_empty).showMessage(this)
        }
    }

    private fun showDetectionDialog(title: String, context: String, isFresh: Boolean) {
        SweetAlertDialog(
            this,
            if (isFresh) SweetAlertDialog.SUCCESS_TYPE else SweetAlertDialog.ERROR_TYPE
        )
            .setTitleText(title)
            .setContentText(context)
            .setConfirmButton("OK") {
                it.dismissWithAnimation()
            }
            .show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_gallery -> {
                startGallery()
            }
            R.id.btn_camera -> {
                startCamera()
            }
            R.id.btn_detection -> {
                uploadImage()
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}