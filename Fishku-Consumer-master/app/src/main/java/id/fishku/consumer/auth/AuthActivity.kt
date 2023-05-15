package id.fishku.consumer.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate: auth")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}