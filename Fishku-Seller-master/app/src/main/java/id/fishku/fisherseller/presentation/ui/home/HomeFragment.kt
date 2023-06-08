package id.fishku.fisherseller.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.FragmentHomeBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.MainViewModelFactory
import id.fishku.fisherseller.presentation.ui.add.AddFActivity
import id.fishku.fisherseller.presentation.ui.maps.MapsActivity
import id.fishku.fisherseller.presentation.ui.prediksi.PrediksiActivity
import id.fishku.fisherseller.presentation.ui.prediksi.PrediksiModel
import id.fishku.fisherseller.presentation.ui.weathers.CurrentWeather
import id.fishku.fisherseller.presentation.ui.weathers.HourlyWave
import id.fishku.fisherseller.presentation.ui.weathers.WeatherModel
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.util.Constants
import id.fishku.fishersellercore.util.capitalizeWords
import id.fishku.fishersellercore.util.hideKeyboard
import id.fishku.fishersellercore.util.mySnackBar
import id.fishku.fishersellercore.view.LottieLoading
import id.fishku.fishersellercore.view.PopDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _searchText: String? = null
    private var _menuList: List<MenuModel>? = null
    private val menuList get() = _menuList ?: listOf()
    private lateinit var menuAdapter: MenuAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var weatherModel: WeatherModel
    private lateinit var prediksiModel: PrediksiModel


    @Inject
    lateinit var prefs: SessionManager

    @Inject
    lateinit var pop: PopDialog

    @Inject
    lateinit var load: LottieLoading

    private lateinit var tvName: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnMaps: ImageButton
    private lateinit var tvWeather: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvFlow: TextView
    private lateinit var tvWave: TextView
    private lateinit var llDate: LinearLayout
    private lateinit var llflow: LinearLayout
    private lateinit var predik: ConstraintLayout
    private lateinit var tvPredik: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val weatherHandler = Handler(Looper.getMainLooper())
    private lateinit var weatherRunnable: Runnable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val data = prefs.getUser()
        tvName = view.findViewById(R.id.tv_user)
        tvName.text = String.format(getString(R.string.hello), data.name)

        val factory = MainViewModelFactory(requireActivity().application)
        weatherModel = factory.create(WeatherModel::class.java)

        binding.home.setOnClickListener {
            binding.home.isFocusableInTouchMode = false
            binding.root.hideKeyboard()
        }

        btnMaps = view.findViewById(R.id.btn_map)
        btnMaps.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intent)
        }

        btnAdd = view.findViewById(R.id.btn_sell)
        btnAdd.setOnClickListener {
            val intent = Intent(requireActivity(), AddFActivity::class.java)
            startActivity(intent)
//            val fire = FirebaseFirestore.getInstance()
//            fire.collection("users").document("fachri@fishku.id").collection("chats").get().addOnSuccessListener { it ->
//                it.documents.forEach {
//                    println("${it.data}")
//                }
//            }

        }

        menuAdapter = MenuAdapter(requireContext())
        binding.rvMenu.apply {
            adapter = menuAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        menuAdapter.setOnItemClick {
            val intent = Intent(requireActivity(), AddFActivity::class.java)
            intent.putExtra(Constants.SEND_MENU_TO_EDIT, it)
            startActivity(intent)
        }

        menuAdapter.setOnDelClick {
            showDelDialog(it)
        }

        predik = view.findViewById(R.id.cl_chart_fish_price)
        predik.setOnClickListener {
            val intent = Intent(requireActivity(), PrediksiActivity::class.java)
            startActivity(intent)
        }
        tvPredik = view.findViewById(R.id.tv_kisaranikan)
        tvPredik.text = "Rp ${prefs.getKisaran()} /Kg"

        observableViewModel()
        getLocation()
        getCurrentTime()
    }


    private fun showDelDialog(data: MenuModel) {
        pop.showDialog(requireContext(),
            positive = { _, _ ->
                observableDelViewModel(data)
            }, negative = { _, _ ->

            },
            title = getString(R.string.sure_delete),
            subTitle = getString(R.string.sure_delete_sub)
        )
    }

    private fun observableDelViewModel(data: MenuModel) {
        viewModel.deleteMenu(data.id_fish).observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> {

                }

                Status.ERROR -> {

                }

                Status.SUCCESS -> {
                    binding.root.mySnackBar(getString(R.string.del_product), R.color.green)
                    observableViewModel()
                }
            }
        }
    }

    private fun sendRequest(textSearch: String) {
        observableSearchViewModel(textSearch)
    }

    private fun observableViewModel() {
        val idSeller = prefs.getUser().id
        viewModel.getListFish(idSeller!!).observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> {
                    loading(true)
                }

                Status.ERROR -> {
                    loading(false)
                }

                Status.SUCCESS -> {
                    loading(false)
                    val empty = res.data?.banyak
                    if (empty == 0)
                        binding.tvNoData.isVisible = true
                    menuAdapter.submitList(res.data?.data)
                    _menuList = res.data?.data as List<MenuModel>
                }
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading)
            binding.shimmerLoading.visibility = View.VISIBLE
        else
            binding.shimmerLoading.visibility = View.GONE
    }

    private fun observableSearchViewModel(textSearch: String) {
        val searchText = textSearch.capitalizeWords()
        val search = menuList.filter { it.name.contains(searchText) }
        menuAdapter.submitList(search)
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            _searchText = s.toString()
            sendRequest(s.toString())
        }

        override fun afterTextChanged(s: Editable) {
            // ignore

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observableWeathers(latitude: Double, longitude: Double) {

        fun updateWeatherData(currentWeather: CurrentWeather?) {
            if (currentWeather != null) {
                llDate = requireView().findViewById(R.id.ll_date)
                llDate.isVisible = true
                tvWeather = requireView().findViewById(R.id.tv_weather)
                tvWeather.text = currentWeather.temperature.toString()
                tvWind = requireView().findViewById(R.id.tv_wind)
                tvWind.text = "${currentWeather.windspeed} m/s"
                generateTime(currentWeather.time)
            }
        }

        weatherModel.weather.observe(viewLifecycleOwner, ::updateWeatherData)
        weatherRunnable = Runnable {
            weatherModel.getWeather(latitude, longitude)
            weatherHandler.postDelayed(weatherRunnable, 5000) // Memperbarui setiap 10 detik
        }
        weatherHandler.post(weatherRunnable)
    }


    private fun observableHourlyWave(latitude: Double, longitude: Double) {
        fun updateHourlyWaveData(hourlyWave: HourlyWave) {
            if (hourlyWave != null) {
                llflow = requireView().findViewById(R.id.ll_flow)
                llflow.isVisible = true
                hourlyWave.time.forEach { time ->
                    if (time == prefs.getTime()) {
                        val index = hourlyWave.time.indexOf(time)
                        val wave = hourlyWave.waveHeight[index]
                        prefs.setWave(wave.toString())
                    }
                }

                tvFlow = requireView().findViewById(R.id.tv_flow)
                tvFlow.text = prefs.getWave()

                if (prefs.getWave().toDouble() > 5 && prefs.getWave().toDouble() < 10) {
                    tvWave = requireView().findViewById(R.id.wave_height)
                    tvWave.text = "Sedang"
                } else if (prefs.getWave().toDouble() > 10) {
                    tvWave = requireView().findViewById(R.id.wave_height)
                    tvWave.text = "Tinggi"
                } else {
                    tvWave = requireView().findViewById(R.id.wave_height)
                    tvWave.text = "Rendah"
                }
            }
        }
        weatherModel.hourly.observe(viewLifecycleOwner, ::updateHourlyWaveData)
        weatherRunnable = Runnable {
            weatherModel.getHourly(latitude, longitude)
            weatherHandler.postDelayed(weatherRunnable, 5000) // Memperbarui setiap 10 detik
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String {
        val mm = "00"
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:$mm")
        val formatted = current.format(formatter)
        prefs.setTime(formatted)
        return formatted
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateTime(time: String) {
        val dateTime: LocalDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)
        val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id"))
        val dayOfMonth = dateTime.get(ChronoField.DAY_OF_MONTH)
        val month = dateTime.month.getDisplayName(TextStyle.FULL, Locale("id"))

        val formatted = "$dayOfWeek, $dayOfMonth $month"
        tvDate = requireView().findViewById(R.id.tv_date)
        tvDate.text = formatted
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            loading(true)
            fusedLocationClient.lastLocation.addOnSuccessListener(
                requireActivity(),
                OnSuccessListener<Location> { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        observableWeathers(latitude, longitude)
                        observableHourlyWave(latitude, longitude)
                        Log.e("TAG", "getLocation: $latitude $longitude")
                        loading(false)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Tidak dapat mendapatkan lokasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(requireActivity(), "Izin akses lokasi ditolak", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}