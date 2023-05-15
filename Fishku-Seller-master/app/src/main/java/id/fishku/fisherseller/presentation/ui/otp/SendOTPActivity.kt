package id.fishku.fisherseller.presentation.ui.otp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.databinding.ActivitySendOtpactivityBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.auth.LoginActivity
import id.fishku.fisherseller.presentation.ui.auth.RegisterActivity
import id.fishku.fishersellercore.data.LocalData
import id.fishku.fishersellercore.requests.Component
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.requests.Parameter
import id.fishku.fishersellercore.requests.Template
import id.fishku.fishersellercore.util.Constants.RANGE_CODE_FIRST
import id.fishku.fishersellercore.util.helper.OtpArgs
import id.fishku.fishersellercore.view.LottieLoading
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Send o t p activity
 *
 * Page for send otp via WhatsApp
 */
@AndroidEntryPoint
class SendOTPActivity : AppCompatActivity() {
    companion object {
        const val NUMBER = "number_resend_otp"
    }


    private var _binding: ActivitySendOtpactivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SendOtpViewModel by viewModels()

    @Inject
    lateinit var saveToLocal: LocalData

    @Inject
    lateinit var loading: LottieLoading

    @Suppress("DEPRECATION")
    private val otpArgs: OtpArgs?
        get() = intent.getParcelableExtra(RegisterActivity.NUMBER)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySendOtpactivityBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.waCard.setOnClickListener {
            sendOtp()
        }
        binding.btnBack.setOnClickListener {
            back()
        }
        init()
        otpResponse()
        startGenerateOtp()
    }

    /**
     * Init
     * Show element in first open
     */
    private fun init() {
        binding.tvNumber.text = otpArgs?.user?.phone_number ?: saveToLocal.getNumber()

    }

    /**
     * Back
     * Back to login page and clear stack page
     */
    private fun back() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    /**
     * Send otp
     * Send Otp to server
     */
    private fun sendOtp() {
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
                Status.LOADING -> loading.start(this)
                Status.ERROR -> {
                    loading.stop()
                }
                Status.SUCCESS -> {
                    loading.stop()
                    startToVerifyOTP()
                }
            }
        }
    }

    /**
     * Start to verify o t p
     * After receive response page will move to verify activity
     *  with data
     */
    private fun startToVerifyOTP() {
        if (otpArgs != null) {
            val intent = Intent(this, VerifyOTPActivity::class.java)
            intent.putExtra(NUMBER, otpArgs)
            startActivity(intent)
        }
    }

    /**
     * Start generate otp
     * Generate otp code locally with coroutine
     */
    private fun startGenerateOtp() {
        lifecycleScope.launch {
            val random = getRandomNum()
            saveOtpCode(random)
        }
    }

    /**
     * Get random num
     * generate random number from RANGE_CODE_FIRST
     * @return
     */
    private fun getRandomNum(): Int {
        return (RANGE_CODE_FIRST).shuffled().first()
    }

    private fun saveOtpCode(code: Int) {
        saveToLocal.setCodeOtp(code)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}