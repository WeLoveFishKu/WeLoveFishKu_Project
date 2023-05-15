package id.fishku.consumer.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.ui.MarketAdapter
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentMarketBinding
import id.fishku.consumer.search.SearchViewModel

@AndroidEntryPoint
class MarketFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding

    private val marketViewModel: MarketViewModel by viewModels()
    private val marketAdapter: MarketAdapter by lazy { MarketAdapter(::productItemClicked) }

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMarketData()
        setupAction()
        searchFish()
        setupCart()
    }

    private fun setupMarketData() {
        marketViewModel.getAllFish().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding?.apply {
                        loadingSearch.visibility = View.VISIBLE
                        rvMarket.visibility = View.GONE
                        imgEmptySearch.visibility = View.GONE
                        tvEmptySearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.GONE
                        tvErrorSearch.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    binding?.loadingSearch?.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) {
                        marketAdapter.submitList(it.data)
                        setupAdapter()
                        isEmptyResult(false)
                    } else {
                        isEmptyResult(true)
                    }
                }
                is Resource.Error -> {
                    binding?.apply {
                        loadingSearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.VISIBLE
                        tvErrorSearch.visibility = View.VISIBLE
                    }
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    private fun setupAdapter() {
        binding?.rvMarket?.apply {
            setHasFixedSize(true)
            adapter = marketAdapter
        }
    }

    private fun setupAction() {
        val menuItem = binding?.toolbarMarket?.menu?.getItem(0)?.actionView
        menuItem?.setOnClickListener(this)
    }

    private fun searchFish() {
        binding?.searchViewFish?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        searchViewModel.searchFish(query)
                        setupSearchData()
                    }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        setupMarketData()
                        clearFocus()
                    }
                    return true
                }
            })
        }
    }

    private fun setupSearchData() {
        searchViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding?.apply {
                        loadingSearch.visibility = View.VISIBLE
                        rvMarket.visibility = View.GONE
                        imgEmptySearch.visibility = View.GONE
                        tvEmptySearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.GONE
                        tvErrorSearch.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    binding?.loadingSearch?.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) {
                        marketAdapter.submitList(it.data)
                        setupAdapter()
                        isEmptyResult(false)
                    } else {
                        isEmptyResult(true)
                    }
                }
                is Resource.Error -> {
                    binding?.apply {
                        loadingSearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.VISIBLE
                        tvErrorSearch.visibility = View.VISIBLE
                    }
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    private fun productItemClicked(fish: Fish) {
        val toDetailFish =
            MarketFragmentDirections.actionNavigationMarketToDetailFishActivity()
        toDetailFish.fishID = fish.fishID
        binding?.root?.findNavController()
            ?.navigate(toDetailFish)
    }

    private fun isEmptyResult(isEmpty: Boolean) {
        if (!isEmpty) {
            binding?.apply {
                rvMarket.visibility = View.VISIBLE
                imgEmptySearch.visibility = View.GONE
                tvEmptySearch.visibility = View.GONE
            }
        } else {
            binding?.apply {
                rvMarket.visibility = View.GONE
                imgEmptySearch.visibility = View.VISIBLE
                tvEmptySearch.visibility = View.VISIBLE
            }
        }
    }

    private fun setupCart() {
        val menuItem = binding?.toolbarMarket?.menu?.getItem(0)?.actionView
        val tvAmount = menuItem?.findViewById<TextView>(R.id.cart_badge)
        marketViewModel.cart.observe(this) {
            when (it) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    val amount = it.data?.size
                    if (amount == 0) {
                        tvAmount?.visibility = View.GONE
                    } else {
                        tvAmount?.visibility = View.VISIBLE
                        tvAmount?.text = amount?.toString()
                    }
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun showBottomSheet() {
        val modalBottomSheet = FilterListDialogFragment()
        modalBottomSheet.show(childFragmentManager, modalBottomSheet.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cart -> v.findNavController()
                .navigate(R.id.action_navigation_market_to_cartActivity)
        }
    }
}