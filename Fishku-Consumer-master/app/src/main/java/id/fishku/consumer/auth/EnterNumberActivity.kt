package id.fishku.consumer.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.auth.register.RegisterFragment
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.domain.model.OtpArgs
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.databinding.ActivityEnterNumberBinding
import id.fishku.consumer.otp.SendOTPActivity
import id.fishku.consumer.utils.Constants.COUNTRY_CODE
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
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    /**
     * Check format number next
     * Check correct format phone number before send to otp activity
     */
    private fun checkFormatNumberNext(){
        val numText = binding.edtNumber.text.trim().toString()
        val startText = numText.startsWith(COUNTRY_CODE)
        if (startText && numText.length == 13){
            saveToLocal.setNumber(numText)
            saveToLocal.setStateAuth(false)
            val user = User(phoneNumber = numText)
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