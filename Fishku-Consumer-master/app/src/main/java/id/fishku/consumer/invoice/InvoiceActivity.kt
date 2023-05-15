package id.fishku.consumer.invoice

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.fishku.consumer.databinding.ActivityInvoiceBinding

class InvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupWebview()
    }

    private fun setupToolbar() {
        binding.toolbarInvoice.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        val url = intent.getStringExtra(EXTRA_URL)

        val webviewInvoice = binding.webviewInvoice.apply {
            settings.javaScriptEnabled = true
        }
        if (url != null) {
            webviewInvoice.loadUrl(url)
        }
    }

    companion object {
        const val EXTRA_URL = "id.fishku.consumer.invoice.EXTRA_URL"
    }
}