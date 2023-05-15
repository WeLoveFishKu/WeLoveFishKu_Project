package id.fishku.consumer.editprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.utils.showError
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.core.utils.uriToFile
import id.fishku.consumer.databinding.ActivityEditProfileBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAction()
    }

    private fun setupToolbar() {
        binding.toolbarEditProfile.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupAction() {
        binding.btnEditProfile.setOnClickListener { editProfileHandler() }
        binding.tvChangePhoto.setOnClickListener { changePhotoHandler() }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile
            binding.imgProfilePhoto.setImageURI(selectedImg)
        }
    }

    private fun changePhotoHandler() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun editProfileHandler() {
        if (!isFormValid()) return

        val name = binding.edtNameProfile.text.toString().toRequestBody("text/plain".toMediaType())
        val phoneNumber = binding.edtPhoneProfile.text.toString().toRequestBody("text/plain".toMediaType())
        val address = binding.edtAddressProfile.text.toString().toRequestBody("text/plain".toMediaType())

        if (getFile != null) {
            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photo = MultipartBody.Part.createFormData(
                "file", // Nama Key di Request Params API
                file.name, requestImageFile
            )
            editProfileViewModel.editProfile(name, phoneNumber, address, photo).observe(this) {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnEditProfile.isEnabled = false
                        binding.loadingEditProfile.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.btnEditProfile.isEnabled = true
                        binding.loadingEditProfile.visibility = View.GONE
                        finish()
                    }
                    is Resource.Error -> {
                        binding.btnEditProfile.isEnabled = true
                        binding.loadingEditProfile.visibility = View.GONE
                    }
                }
            }
        } else {
            getString(R.string.photo_cannot_empty).showMessage(this@EditProfileActivity)
        }
    }

    private fun isFormValid(): Boolean {
        val name = binding.edtNameProfile.text.toString()
        val phoneNumber = binding.edtPhoneProfile.text.toString()
        val address = binding.edtAddressProfile.text.toString()
        val photo = binding.imgProfilePhoto.toString()

        binding.tilNameProfile.apply {
            if (name.isEmpty()) {
                showError(true, getString(R.string.email_cannot_be_empty))
            } else {
                showError(false)
            }
        }

        binding.tilPhoneProfile.apply {
            if (phoneNumber.isEmpty()) {
                showError(true, getString(R.string.password_cannot_be_empty))
            } else {
                showError(false)
            }
        }

        binding.tilAddressProfile.apply {
            if (address.isEmpty()) {
                showError(true, getString(R.string.password_cannot_be_empty))
            } else {
                showError(false)
            }
        }

        binding.imgProfilePhoto.apply {
            if (photo.isEmpty()) {
                getString(R.string.photo_cannot_empty).showMessage(this@EditProfileActivity)
            }
        }

        return !binding.tilNameProfile.isErrorEnabled && !binding.tilPhoneProfile.isErrorEnabled && !binding.tilAddressProfile.isErrorEnabled && photo.isNotEmpty()
    }

}