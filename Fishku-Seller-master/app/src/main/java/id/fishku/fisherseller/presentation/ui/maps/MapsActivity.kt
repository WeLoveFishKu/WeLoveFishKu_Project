package id.fishku.fisherseller.presentation.ui.maps

import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityMapsBinding
import id.fishku.fisherseller.presentation.ui.Data.FakeDataFish
import id.fishku.fisherseller.seller.services.SessionManager
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //fish layout
    private lateinit var imgFish: ImageView
    private lateinit var tvFishName: TextView
    private lateinit var tvFishPrice: TextView
    private lateinit var tvFishDesc: TextView

    @Inject
    lateinit var prefs: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        mMap.setOnMarkerClickListener { marker ->
            showDialog(marker)
            true
        }

        getMyLocation()
        markerFish()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        prefs.setLongitude(longitude.toString())
                        prefs.setLatitude(latitude.toString())
                    }
                }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun markerFish() {
        FakeDataFish.dummyDataFish.forEach { fish ->
            val latLng = LatLng(fish.lat.toDouble(), fish.lon.toDouble())
            mMap.addMarker(
                MarkerOptions().position(latLng).title(fish.title).snippet(fish.id.toString())
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        }
    }

    private fun showDialog(marker: Marker) {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.detail_fish)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val title = dialog.findViewById<TextView>(R.id.txtFishName)
        val desc = dialog.findViewById<TextView>(R.id.txtFishDescription)
        val img = dialog.findViewById<ImageView>(R.id.imgFish)

        if (marker.snippet != null) {
            val id = marker.snippet!!.toInt() - 1
            val fish = FakeDataFish.dummyDataFish[id]
            title.text = fish.title
            desc.text = fish.description
            Glide.with(this).load(fish.image).into(img)
        }

        dialog.show()
    }
}


