package id.fishku.consumer.otp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.auth.login.LoginViewModel
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.data.source.remote.request.Component
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.request.Parameter
import id.fishku.consumer.core.data.source.remote.request.Template
import id.fishku.consumer.databinding.ActivityVerifyOtpactivityBinding
import id.fishku.consumer.main.MainActivity
import id.fishku.consumer.utils.Constants.DELAY_SECONDS
import id.fishku.consumer.utils.Constants.RANGE_CODE_SECOND
import id.fishku.consumer.utils.Constants.WAITING_MINUTES
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Verify o t p activity
 *
 * Page for verify code send to wa and locally
 */
@AndroidEntryPoint
class VerifyOTPActivity : AppCompatActivity() {

    private var _binding: ActivityVerifyOtpactivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SendOtpViewModel by viewModels()
    private val viewModelLogin: LoginViewModel by viewModels()

    @Inject
    lateinit var saveToLocal: LocalData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnVerify.setOnClickListener {
            loadCheckCode()
        }
        binding.tvResend.setOnClickListener {
            resendOtp()
            startGenerateOtp()
            textVisibility(false)
        }
        binding.btnBack.setOnClickListener {
            back()
        }
        init()
        startGenerateOtp()
        otpResponse()
    }

    /**
     * Init
     * Show element in first open
     */
    private fun init() {
        binding.tvNum.text = saveToLocal.getNumber()

    }

    /**
     * Back
     * Back to send otp page and clear stack page
     */
    private fun back() {
        val intent = Intent(this, SendOTPActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    /**
     * Resend otp
     * Resend otp if after 60 not input correct code
     * after 60 seconds this button will visible
     */
    private fun resendOtp() {
        val codeOtp = saveToLocal.getCodeOtp()
        val user = saveToLocal.getDataUser()
        val components = Component(
            parameters = listOf(
                Parameter(
                    text = "$codeOtp"
                )
            )
        )
        val template = Template(
            components = listOf(
                components
            )
        )
        val otpRequest = OtpRequest(
            to = "${user.phoneNumber}",
            template = template
        )
        viewModel.sendOtpCode(otpRequest)
    }

    /**
     * Otp response
     * Get otp response from server
     */
    private fun otpResponse() {
        viewModel.otpResponse.observe(this) { res ->
        }
    }

    /**
     * Start generate otp
     * Generate otp code locally with coroutine
     */
    @SuppressLint("StringFormatInvalid")
    private fun startGenerateOtp() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                var count = WAITING_MINUTES
                while (count > 0) {
                    count -= 1
                    delay(DELAY_SECONDS)
                    binding.tvCount.text =
                        resources.getString(R.string.waiting_code, count.toString())
                    if (count == 0) {
                        val random = getRandomNum()
                        saveOtpCode(random)
                    }
                }
            }
        }
    }

    private fun loadCheckCode() {
        lifecycleScope.launch {
            isLoading(true)
            delay(DELAY_SECONDS)
            checkVerificationCode()
            isLoading(false)
        }
    }

    /**
     * Check verification code
     * check if code from local otp and wa otp is matching
     */
    private fun checkVerificationCode() {
        val verifyCode = binding.edtVerifyCode.text.trim().toString()
        val localCode = saveToLocal.getCodeOtp()
        val state = saveToLocal.getStateAuth()
        val user = saveToLocal.getDataUser()
        if (verifyCode.isNotEmpty()) {
            val codeWa = verifyCode.toInt()
            if (codeWa == localCode) {
                if (state) {
                    viewModelLogin.saveSession(user.token.toString(), user)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                } else {
                    toRegisterFragment()
                }
            } else {
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Get random num
     * generate random number from RANGE_CODE_SECOND
     * @return
     */
    private fun getRandomNum(): Int {
        return (RANGE_CODE_SECOND).shuffled().first()
    }

    /**
     * Save otp code
     * save otp locally and use later
     * @param code
     */
    private fun saveOtpCode(code: Int) {
        saveToLocal.setCodeOtp(code)
        textVisibility(true)
    }

    private fun textVisibility(value: Boolean = false) {
        if (value) {
            binding.tvResend.visibility = View.VISIBLE
            binding.tvCount.visibility = View.GONE
        } else {
            binding.tvCount.visibility = View.VISIBLE
            binding.tvResend.visibility = View.GONE
        }
    }

    private fun toRegisterFragment() {
        val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
            .setGraph(R.navigation.auth_navigation)
            .setDestination(R.id.registrationFragment)
            .createPendingIntent()

        pendingIntent.send()
    }

    private fun isLoading(value: Boolean) {
        if (value)
            binding.loading.visibility = View.VISIBLE
        else
            binding.loading.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}