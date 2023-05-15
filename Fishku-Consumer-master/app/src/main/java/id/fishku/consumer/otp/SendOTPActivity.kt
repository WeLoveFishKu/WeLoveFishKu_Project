package id.fishku.consumer.otp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.auth.AuthActivity
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.data.source.remote.request.Component
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.request.Parameter
import id.fishku.consumer.core.data.source.remote.request.Template
import id.fishku.consumer.databinding.ActivitySendOtpactivityBinding
import id.fishku.consumer.utils.Constants.RANGE_CODE_FIRST
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Send o t p activity
 *
 * Page for send otp via WhatsApp
 */
@AndroidEntryPoint
class SendOTPActivity : AppCompatActivity() {

    private var _binding: ActivitySendOtpactivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SendOtpViewModel by viewModels()

    @Inject
    lateinit var saveToLocal: LocalData

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
        val number = saveToLocal.getNumber()
        number?.let {
            if (number[0] == '0') {
                binding.tvNumber.text = number.replaceRange(0, 1, "62")
            } else {
                binding.tvNumber.text = number
            }
        }
    }

    /**
     * Back
     * Back to login page and clear stack page
     */
    private fun back() {
        val intent = Intent(this, AuthActivity::class.java)
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
        val user = saveToLocal.getDataUser()
        val number = user.phoneNumber
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
        if (number != null) {
            val otpRequest = OtpRequest(
                to = if (number[0] == '0') number.replaceRange(0, 1, "62") else number,
                template = template
            )
            viewModel.sendOtpCode(otpRequest)
        }
//        startActivity(Intent(this, VerifyOTPActivity::class.java))
    }

    /**
     * Otp response
     * Get otp response from server
     */
    private fun otpResponse() {
        viewModel.otpResponse.observe(this) { res ->
            when (res) {
                is Resource.Loading -> {
                    isLoading(true)
                }
                is Resource.Error -> {
                    isLoading(false)
                }
                is Resource.Success -> {
                    isLoading(false)
                    startToVerifyOTP()
                }
            }
        }
    }

    private fun isLoading(value: Boolean) {
        if (value)
            binding.loading.visibility = View.VISIBLE
        else
            binding.loading.visibility = View.GONE
    }

    /**
     * Start to verify o t p
     * After receive response page will move to verify activity
     *  with data
     */
    private fun startToVerifyOTP() {
        val user = saveToLocal.getDataUser()
        if (user.phoneNumber != null) {
            val intent = Intent(this, VerifyOTPActivity::class.java)
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