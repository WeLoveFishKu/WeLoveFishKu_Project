package id.fishku.fisherseller.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.FragmentHomeBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.add.AddFActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.util.Constants
import id.fishku.fishersellercore.util.capitalizeWords
import id.fishku.fishersellercore.util.hideKeyboard
import id.fishku.fishersellercore.util.mySnackBar
import id.fishku.fishersellercore.view.LottieLoading
import id.fishku.fishersellercore.view.PopDialog
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

    @Inject
    lateinit var prefs: SessionManager
    @Inject
    lateinit var pop: PopDialog
    @Inject
    lateinit var load: LottieLoading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.home.setOnClickListener {
            binding.home.isFocusableInTouchMode = false
            binding.root.hideKeyboard()
        }


        binding.btnAdd.setOnClickListener{
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
        binding.edtSearch.addTextChangedListener(afterTextChangedListener)
        binding.edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val textSearch = v.text.toString()
                sendRequest(textSearch)
                return@OnEditorActionListener true
            }
            false
        })
        binding.inputSearch.setEndIconOnClickListener {
            sendRequest(searchText)
        }
        observableViewModel()
    }

    private fun showDelDialog(data: MenuModel){
        pop.showDialog(requireContext(),
            positive = {_,_ ->
                observableDelViewModel(data)
        }, negative = {_,_->

        },
            title = getString(R.string.sure_delete),
            subTitle = getString(R.string.sure_delete_sub)
        )
    }

    private fun observableDelViewModel(data: MenuModel){
                    viewModel.deleteMenu(data.id_fish).observe(viewLifecycleOwner){ res ->
                when(res.status){
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {

                    }
                    Status.SUCCESS ->{
                        binding.root.mySnackBar(getString(R.string.del_product), R.color.green)
                        observableViewModel()
                    }
                }
            }
    }

    private fun sendRequest(textSearch: String){
        observableSearchViewModel(textSearch)
    }

    private fun observableViewModel(){
        val idSeller = prefs.getUser().id
        viewModel.getListFish(idSeller!!).observe(viewLifecycleOwner){res ->
            when(res.status){
               Status.LOADING -> {
                    loading(true)
                }
                Status.ERROR -> {
                    loading(false)
                }
                Status.SUCCESS ->{
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

    private fun loading(isLoading: Boolean){
        if (isLoading)
            binding.shimmerLoading.visibility = View.VISIBLE
        else
            binding.shimmerLoading.visibility = View.GONE
    }

    private fun observableSearchViewModel( textSearch: String){
        val searchText = textSearch.capitalizeWords()
        val search = menuList.filter{it.name.contains(searchText)}
        println(search)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}