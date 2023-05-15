package id.fishku.fisherseller.presentation.ui.otp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityVerifyOtpactivityBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.DashboardActivity
import id.fishku.fisherseller.presentation.ui.auth.RegisterActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.data.LocalData
import id.fishku.fishersellercore.requests.Component
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.requests.Parameter
import id.fishku.fishersellercore.requests.Template
import id.fishku.fishersellercore.util.Constants.DELAY_SECONDS
import id.fishku.fishersellercore.util.Constants.RANGE_CODE_SECOND
import id.fishku.fishersellercore.util.Constants.WAITING_MINUTES
import id.fishku.fishersellercore.util.helper.OtpArgs
import id.fishku.fishersellercore.view.LottieLoading
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
    companion object{
        const val TO_REG = "num_reg"
    }
    private var _binding: ActivityVerifyOtpactivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SendOtpViewModel by viewModels()

    @Inject
    lateinit var saveToLocal: LocalData

    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var loading: LottieLoading

    @Suppress("DEPRECATION")
    private val otpArgs: OtpArgs?
        get() = intent.getParcelableExtra(SendOTPActivity.NUMBER)

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
    private fun init(){
        binding.tvNum.text = otpArgs?.user?.phone_number ?: saveToLocal.getNumber()

    }

    /**
     * Back
     * Back to send otp page and clear stack page
     */
    private fun back(){
        val intent = Intent(this, SendOTPActivity::class.java)
        startActivity(intent)
    }

    /**
     * Resend otp
     * Resend otp if after 60 not input correct code
     * after 60 seconds this button will visible
     */
    private fun resendOtp() {
        val codeOtp = saveToLocal.getCodeOtp()
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
            to = "${otpArgs?.user?.phone_number}",
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
            when (res.status) {
                Status.LOADING -> isLoading(true)
                Status.ERROR -> {
                    isLoading(false)
                }
                Status.SUCCESS -> {
                    isLoading(false)
                    loading.stop()
                }
            }
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

    private fun loadCheckCode(){
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
        if (verifyCode.isNotEmpty()) {
            val codeWa = verifyCode.toInt()
            if (codeWa == localCode) {
                if (otpArgs?.state == true){
                    startActivity(Intent(this, DashboardActivity::class.java))
                    session.setUser(otpArgs?.user)
                    session.setLogin(true)
                }else{
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra(TO_REG, otpArgs)
                    startActivity(intent)
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

    private fun textVisibility(value: Boolean = false){
        if (value){
            binding.tvResend.visibility = View.VISIBLE
            binding.tvCount.visibility = View.GONE
        }else{
            binding.tvCount.visibility = View.VISIBLE
            binding.tvResend.visibility = View.GONE
        }
    }
    private fun isLoading(value: Boolean){
        if (value)
            loading.start(this)
        else
            loading.stop()
    }
    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}