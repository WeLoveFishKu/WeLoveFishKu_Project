package id.fishku.fisherseller.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityRegisterBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.otp.SendOTPActivity
import id.fishku.fisherseller.presentation.ui.otp.VerifyOTPActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.util.*
import id.fishku.fishersellercore.util.helper.OtpArgs
import id.fishku.fishersellercore.util.helper.User
import id.fishku.fishersellercore.view.LottieLoading
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener  {

    companion object{
        const val NUMBER = "number"
    }

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    private var _roles: String? = null
    private val roles get() = _roles!!

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var location: EditText

    @Inject
    lateinit var prefs: SessionManager
    @Inject
    lateinit var loading: LottieLoading

    @Suppress("DEPRECATION")
    private val otpArgs: OtpArgs?
        get() = intent.getParcelableExtra(VerifyOTPActivity.TO_REG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        initVar()


        ArrayAdapter.createFromResource(
            this,
            R.array.roles_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnRole.adapter = adapter
        }

        binding.tvRegister.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.spnRole.onItemSelectedListener = this
        // validate
        validateField()
    }

    private fun initVar(){
        with(binding){
            name = edtUser
            email = edtEmail
            password = edtPw
            location = edtAddress
        }
    }

    private fun sendRequest(){

        val name = name.textTrim()
        val email = email.textTrim()
        val password = password.textTrim()
        val location = location.textTrim()
        val number = otpArgs?.user?.phone_number!!
            val request = RegisterRequest(name, email, password, number, location, roles)
            observableViewModel(request)

    }

    private fun observableViewModel(request: RegisterRequest){
        viewModel.register(request).observe(this){ res ->
            when(res.status){
                Status.LOADING -> loading.start(this)
                Status.ERROR -> {
                    loading.stop()
                    binding.root.mySnackBar(getString(R.string.register_error))
                }
                Status.SUCCESS -> {
                    loading.stop()
                    startToOTP()
                    finish()
                }
            }
        }
    }

    private fun startToOTP(){
        val userData = User(
            name = name.textTrim(),
            email = email.textTrim(),
            phone_number = otpArgs?.user?.phone_number,
            location = location.textTrim(),
            roles = roles,
        )

        if (otpArgs?.user?.phone_number != null){
            val intent = Intent(this, SendOTPActivity::class.java)
            intent.putExtra(NUMBER, OtpArgs(userData))
            startActivity(intent)
        }
    }

    private fun validateField(){
        with(binding){
            edtEmail.validate(resources.getString(R.string.email_invalid)) { s1 -> s1.isValidEmail() }
            edtPw.validate(resources.getString(R.string.min_char_pw)) { s1 -> s1.length >=6 }
            edtEmail.addTextChangedListener(afterTextChangedListener)
            edtPw.addTextChangedListener(afterTextChangedListener)
            edtUser.addTextChangedListener(afterTextChangedListener)
            edtAddress.addTextChangedListener(afterTextChangedListener)
            edtEmail.clearError()
            edtPw.clearError()
        }
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding){
                val usernameInput: String = edtUser.textTrim()
                val addressInput: String = edtAddress.textTrim()

                btnRegister.isEnabled = (
                        usernameInput.isNotBlank()
                                && usernameInput.isNotEmpty()
                                && addressInput.isNotBlank()
                                && addressInput.isNotEmpty())
            }
        }

        override fun afterTextChanged(s: Editable) {
            binding.btnRegister.isEnabled = (binding.edtEmail.error.isNullOrBlank() && binding.edtPw.error.isNullOrBlank())


            // ignore
        }
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item = p0?.selectedItem
        _roles = item as String
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_register -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.btn_register -> {
                sendRequest()
            }
            R.id.btn_back -> {
                val intent = Intent(this, SendOTPActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}