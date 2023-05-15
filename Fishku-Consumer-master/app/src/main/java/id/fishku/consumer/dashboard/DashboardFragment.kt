package id.fishku.consumer.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.ui.DashboardAdapter
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentDashboardBinding

@AndroidEntryPoint
class DashboardFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val bestProductAdapter: DashboardAdapter by lazy { DashboardAdapter(::productItemClicked) }
    private val recommendationAdapter: DashboardAdapter by lazy { DashboardAdapter(::productItemClicked) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupAction()
        searchFish()
        setupCart()
    }

    private fun setupData() {
        dashboardViewModel.getAllFish().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> bestProductLoading(true)

                is Resource.Success -> {
                    bestProductLoading(false)
                    if (!it.data.isNullOrEmpty()) {
                        bestProductAdapter.submitList(it.data)
                        setBestAdapter()
                    } else {
                        binding?.tvBestProductEmpty?.visibility = View.VISIBLE
                        binding?.loadingBestProduct?.visibility = View.INVISIBLE
                        changeConstraint(true)
                    }
                }

                is Resource.Error -> {
                    bestProductLoading(true)
                    it.message?.showMessage(requireContext())
                }
            }
        }

        dashboardViewModel.getAllFish().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> recommendProductLoading(true)

                is Resource.Success -> {
                    recommendProductLoading(false)
                    if (!it.data.isNullOrEmpty()) {
                        recommendationAdapter.submitList(it.data)
                        setRecommendationAdapter()
                    } else {
                        binding?.tvRecommendProductEmpty?.visibility = View.VISIBLE
                        binding?.loadingRecommendProduct?.visibility = View.INVISIBLE
                    }
                }

                is Resource.Error -> {
                    recommendProductLoading(true)
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    private fun setBestAdapter() {
        binding?.rvBestProduct?.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = bestProductAdapter
        }
    }

    private fun setRecommendationAdapter() {
        binding?.rvRecommendation?.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = recommendationAdapter
        }
    }

    private fun setupCart() {
        val menuItem = binding?.toolbarDashboard?.menu?.getItem(0)?.actionView
        val tvAmount = menuItem?.findViewById<TextView>(R.id.cart_badge)
        dashboardViewModel.cart.observe(this) {
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

    private fun setupAction() {
        val menuItem = binding?.toolbarDashboard?.menu?.getItem(0)?.actionView
        menuItem?.setOnClickListener(this)
        binding?.apply {
            btnShowAllBest.setOnClickListener(this@DashboardFragment)
            btnShowAllRecommendation.setOnClickListener(this@DashboardFragment)
        }
    }

    private fun searchFish() {
        binding?.apply {
            searchViewFish.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val toSearchAcitivy =
                        DashboardFragmentDirections.actionNavigationDashboardToSearchActivity(query)
                    root.findNavController().navigate(toSearchAcitivy)
                    searchViewFish.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            })
        }
    }

    private fun productItemClicked(fish: Fish) {
        val toDetailFish =
            DashboardFragmentDirections.actionNavigationDashboardToDetailFishActivity()
        toDetailFish.fishID = fish.fishID
        binding?.root?.findNavController()?.navigate(toDetailFish)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cart -> v.findNavController()
                .navigate(R.id.action_navigation_dashboard_to_cartActivity)
            R.id.btn_show_all_best -> {
                val toSearchAcitivy =
                    DashboardFragmentDirections.actionNavigationDashboardToSearchActivity("")
                binding?.root?.findNavController()?.navigate(toSearchAcitivy)
            }
            R.id.btn_show_all_recommendation -> {
                val toSearchAcitivy =
                    DashboardFragmentDirections.actionNavigationDashboardToSearchActivity("")
                binding?.root?.findNavController()?.navigate(toSearchAcitivy)
            }
        }
    }

    private fun changeConstraint(isChange: Boolean) {
        val params =
            binding?.tvRecommendation?.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = if (isChange) {
            binding?.loadingBestProduct?.id!!
        } else {
            binding?.rvBestProduct?.id!!
        }
        binding?.tvRecommendation?.requestLayout()
    }

    private fun bestProductLoading(isLoading: Boolean) {
        changeConstraint(isLoading)
        if (isLoading) {
            binding?.apply {
                loadingBestProduct.visibility = View.VISIBLE
                rvBestProduct.visibility = View.GONE
            }
        } else {
            binding?.apply {
                loadingBestProduct.visibility = View.GONE
                rvBestProduct.visibility = View.VISIBLE
            }
        }
    }

    private fun recommendProductLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.apply {
                loadingRecommendProduct.visibility = View.VISIBLE
                rvRecommendation.visibility = View.GONE
            }
        } else {
            binding?.apply {
                loadingRecommendProduct.visibility = View.GONE
                rvRecommendation.visibility = View.VISIBLE
            }
        }
    }
}