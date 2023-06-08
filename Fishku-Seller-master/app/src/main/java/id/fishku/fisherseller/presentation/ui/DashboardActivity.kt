package id.fishku.fisherseller.presentation.ui

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityDashboardBinding
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.services.RemoteConfig
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var prefs: SessionManager

    private var doubleBackPressed = false
    private val doubleBackHandler = Handler()
    private val doubleBackRunnable = Runnable {
        doubleBackPressed = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDashboardBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_dashboard)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_menu,
                R.id.navigation_order,
                R.id.navigation_chat,
                R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        remoteConfig.initRemoteConfig(this)
    }

    // make on double back pressed to exit
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackPressed) {
            // Perform action to exit the app
            showExitConfirmationDialog()
        } else {
            doubleBackPressed = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            doubleBackHandler.postDelayed(doubleBackRunnable, 2000) // Delay of 2 seconds
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}