package id.fishku.consumer.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.auth.AuthActivity
import id.fishku.consumer.databinding.ActivitySettingBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener { logoutHandler() }
        binding.btnCriticsSuggestion.setOnClickListener { sendEmail(this) }
        binding.btnSupportFishku.setOnClickListener { supportFishku() }
    }

    private fun logoutHandler() {
        val builder =
            AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.view_logout_dialog, null)
        val btnYes = view.findViewById<Button>(R.id.btn_logout_yes)
        val btnNo = view.findViewById<Button>(R.id.btn_logout_no)
        btnYes.setOnClickListener {
            lifecycleScope.launch {
                settingViewModel.deleteSession()
                settingViewModel.signOutWithGoogle(googleSignInClient)
                delay(500)
                val loginIntent = Intent(this@SettingActivity, AuthActivity::class.java)
                loginIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
                finish()
            }
        }
        btnNo.setOnClickListener {
            builder.dismiss()
        }
        builder.setView(view)
        builder.show()
    }

    private fun sendEmail(context: Context) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_fishku)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        }
        context.startActivity(Intent.createChooser(emailIntent, "Send feedback"))
    }

    private fun supportFishku() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun inAppReview() {
        val manager = ReviewManagerFactory.create(applicationContext)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {

                }
                Log.d("TAG", "inAppReview: $reviewInfo")
            } else {
                Log.d("TAG", "inAppReview2: ${task.exception?.message}")
            }
        }
    }
}