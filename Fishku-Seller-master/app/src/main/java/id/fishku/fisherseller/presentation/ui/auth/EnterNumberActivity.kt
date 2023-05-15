package id.fishku.fisherseller.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.databinding.ActivityEnterNumberBinding
import id.fishku.fisherseller.presentation.ui.otp.SendOTPActivity
import id.fishku.fishersellercore.data.LocalData
import id.fishku.fishersellercore.util.Constants.COUNTRY_CODE
import id.fishku.fishersellercore.util.helper.OtpArgs
import id.fishku.fishersellercore.util.helper.User
import id.fishku.fishersellercore.util.textTrim
import javax.inject.Inject

/**
 * Enter number activity
 *
 * Page for input phone number
 */
@AndroidEntryPoint
class EnterNumberActivity : AppCompatActivity() {
    companion object{
        const val NUMBER = "number"
    }
    private var _binding: ActivityEnterNumberBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var saveToLocal: LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEnterNumberBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            checkFormatNumberNext()
        }
        binding.btnBack.setOnClickListener {
            back()
        }

    }


    /**
     * Back
     * Back page to login page
     */
    private fun back(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    /**
     * Check format number next
     * Check correct format phone number before send to otp activity
     */
    private fun checkFormatNumberNext(){
        val numText = binding.edtNumber.textTrim()
        val startText = numText.startsWith(COUNTRY_CODE)
        if (startText && numText.length == 13){
            saveToLocal.setNumber(numText)
            val user = User(phone_number = numText)
            val intent = Intent(this, SendOTPActivity::class.java)
            intent.putExtra(NUMBER, OtpArgs(user))
            startActivity(intent)
        }else{
            binding.tvError.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}