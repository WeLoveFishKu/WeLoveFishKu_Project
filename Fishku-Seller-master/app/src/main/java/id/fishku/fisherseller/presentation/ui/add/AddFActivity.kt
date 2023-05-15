package id.fishku.fisherseller.presentation.ui.add

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityAddBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.DashboardActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.requests.AddRequest
import id.fishku.fishersellercore.util.*
import id.fishku.fishersellercore.util.Constants.IMAGE_API
import id.fishku.fishersellercore.util.Constants.IMAGE_TYPE
import id.fishku.fishersellercore.util.Constants.REQUEST_CODE_PERMISSIONS
import id.fishku.fishersellercore.util.Constants.REQUIRED_PERMISSIONS
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class AddFActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddViewModel by viewModels()
    private var myFile: File? = null

    private var _editMenu: MenuModel? = null
    private val editMenu get() = _editMenu

    @Inject
    lateinit var prefs: SessionManager

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                binding.root.mySnackBar(getString(R.string.permission))
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            applicationContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        afterTextChangedListener.let {
            with(binding) {
                edtName.addTextChangedListener(it)
                edtPrice.addTextChangedListener(it)
                edtStock.addTextChangedListener(it)
            }
        }
        binding.btnBack.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)

        _editMenu = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Constants.SEND_MENU_TO_EDIT, MenuModel::class.java)
        } else {
            intent.getParcelableExtra(Constants.SEND_MENU_TO_EDIT)
        }
        if (editMenu != null){
            binding.tvHeader.text = resources.getString(R.string.edit_menu)
            binding.tvEdit.visibility = View.GONE
        }
        binding.edtName.setText(editMenu?.name ?: "")
        binding.edtPrice.setText(editMenu?.price ?: "")
        binding.edtStock.setText(editMenu?.weight?.toString() ?: "")
        editMenu?.photo_url?.let { binding.itemImg.setImage(Constants.URL_IMAGE+it) }
    }

    private fun sendRequest() {
        val id = prefs.getUser().id
        val name = binding.edtName.textTrim().capitalizeWords()
        val weight = binding.edtStock.textTrim()
        val desc = binding.edtDesc.textTrim()
        val price = binding.edtPrice.textTrim()

        val request = AddRequest(id!!, name, weight, desc, price, myFile?.name ?: "")
        if (editMenu?.id_fish != null)
            observableEditViewModel(editMenu!!.id_fish, request)
        else {
            observableViewModel(request)
        }
    }

    private fun uploadImage(){

        if (myFile != null) {
            observableUploadViewModel(myFile!!)
        } else{
            sendRequest()
            binding.ivEmpty.visibility = View.VISIBLE
        }
    }

    private fun observableUploadViewModel(file: File) {
        viewModel.uploadImage(IMAGE_API, file).observe(this) {
            sendRequest()
        }
    }

    private fun observableEditViewModel(idFish: String, request: AddRequest) {
        viewModel.editMenu(idFish, request).observe(this) { res ->
            when (res.status) {
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    binding.root.mySnackBar(getString(R.string.add_product_failed))
                }
                Status.SUCCESS -> {
                    binding.root.mySnackBar(getString(R.string.edit_product), R.color.green)
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun observableViewModel(request: AddRequest) {
        viewModel.postMenu(request).observe(this) { res ->
            when (res.status) {
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    binding.root.mySnackBar(getString(R.string.add_product_failed))
                }
                Status.SUCCESS -> {
                    binding.root.mySnackBar(getString(R.string.add_product), R.color.green)
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding) {
                val nameInput = edtName.text.toString().trim()
                val priceInput = edtPrice.text.toString().trim()
                val stockInput = edtStock.text.toString().trim()
                btnAdd.isEnabled = (nameInput.isNotBlank()
                        && nameInput.isNotEmpty()
                        && priceInput.isNotBlank()
                        && priceInput.isNotEmpty()
                        && stockInput.isNotBlank()
                        && stockInput.isNotEmpty()
                        )
            }
        }

        override fun afterTextChanged(s: Editable) {
            // ignore

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = IMAGE_TYPE
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)

    }

    @SuppressLint("ResourceAsColor")
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, applicationContext)
            var imageUri: Uri?
            binding.tvEdit.text = getString(R.string.load_image)
            lifecycleScope.launch {
                val imageFile = Compressor.compress(applicationContext, myFile, Dispatchers.IO) {
                    quality(80)
                    default(width = 520)
                    format(Bitmap.CompressFormat.JPEG)
                    size(397_152)
                }
                imageUri = imageFile.toUri()
                if (imageUri != null) {
                    binding.itemImg.setImageURI(imageUri)
                    binding.tvEdit.text = getString(R.string.edit_photo)
                }
                this@AddFActivity.myFile = imageFile
            }
            binding.itemImg.scaleType = ImageView.ScaleType.CENTER_CROP

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            R.id.btn_add -> {
                uploadImage()
            }
            R.id.tv_edit -> startGallery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}