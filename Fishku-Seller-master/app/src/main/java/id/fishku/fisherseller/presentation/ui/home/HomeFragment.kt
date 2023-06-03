package id.fishku.fisherseller.presentation.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.FragmentHomeBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.MainViewModelFactory
import id.fishku.fisherseller.presentation.ui.add.AddFActivity
import id.fishku.fisherseller.presentation.ui.maps.MapsActivity
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
    private val searchText get() = _searchText ?: ""
    private var _menuList: List<MenuModel>? = null
    private val menuList get() = _menuList ?: listOf()
    private lateinit var menuAdapter: MenuAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var weatherModel: WeatherModel

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
//        binding.edtSearch.addTextChangedListener(afterTextChangedListener)
//        binding.edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val textSearch = v.text.toString()
//                sendRequest(textSearch)
//                return@OnEditorActionListener true
//            }
//            false
//        })
//        binding.inputSearch.setEndIconOnClickListener {
//            sendRequest(searchText)
//        }


        observableViewModel()
        observableWeathers()
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
    private fun observableWeathers() {
        weatherModel.weather.observe(this) {
            if (it != null) {
                tvWeather = requireView().findViewById(R.id.tv_weather)
                tvWeather.text = it.temperature.toString()
                tvWind = requireView().findViewById(R.id.tv_wind)
                tvWind.text = "${it.windspeed} m/s"
                generateTime(it.time)
            }
        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}