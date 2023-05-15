package id.fishku.consumer.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.databinding.ActivityMainBinding
import id.fishku.consumer.invoice.InvoiceActivity
import id.fishku.consumer.services.FirebaseService
import id.fishku.consumer.services.RemoteConfig
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var remoteConfig: RemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val invoiceUrl = intent.getStringExtra(InvoiceActivity.EXTRA_URL)
        if (invoiceUrl != null) {
            val invoiceIntent = Intent(this, InvoiceActivity::class.java)
            invoiceIntent.putExtra(InvoiceActivity.EXTRA_URL, invoiceUrl)
            startActivity(invoiceIntent)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null

        iniRemoteConfig()
        val firebaseService = FirebaseService()

    }

    private fun iniRemoteConfig(){
        remoteConfig.initRemoteConfig(this)
    }
}