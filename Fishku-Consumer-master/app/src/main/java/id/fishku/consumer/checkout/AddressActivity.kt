package id.fishku.consumer.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import id.fishku.consumer.R
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.ActivityAddressBinding
import java.io.IOException
import java.util.*

class AddressActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddressBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getCurrentLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
        mMap.setOnMapClickListener { latLng -> setMarker(latLng) }
        binding.btnMyLocation.setOnClickListener { getCurrentLocation() }
        binding.btnSelectAddress.setOnClickListener { intentResult() }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    setMarker(currentLocation)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMarker(latLng: LatLng) {
        apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
        mMap.apply {
            clear()
            animateCamera(CameraUpdateFactory.newLatLng(latLng))
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.delivery_location))
            )
        }
        binding.edtAddressMap.setText(getDetailAddress(latLng.latitude, latLng.longitude))
    }

    private fun intentResult() {
        val address = binding.edtAddressMap.text.toString()
        if (address.isNotEmpty()) {
            val intentBack = Intent().apply {
                putExtra(EXTRA_ADDRESS, address)
                putExtra(EXTRA_LATITUDE, latitude)
                putExtra(EXTRA_LONGITUDE, longitude)
            }
            setResult(RESULT_OK, intentBack)
            finish()
        } else {
            getString(R.string.address_is_required).showMessage(this)
        }
    }

    @Suppress("DEPRECATION")
    fun getDetailAddress(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    companion object {
        const val EXTRA_ADDRESS = "id.fishku.consumer.checkout.EXTRA_ADDRESS"
        const val EXTRA_LONGITUDE = "id.fishku.consumer.checkout.EXTRA_LONGITUDE"
        const val EXTRA_LATITUDE = "id.fishku.consumer.checkout.EXTRA_LATITUDE"
    }
}