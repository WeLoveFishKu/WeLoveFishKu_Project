package id.fishku.fisherseller.presentation.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.databinding.FragmentOrderBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.order.detail.DetailActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.util.Constants
import id.fishku.fishersellercore.view.LottieLoading
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null

    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private val viewModel: OrderViewModel by viewModels()

    @Inject
    lateinit var prefs: SessionManager
    @Inject
    lateinit var load: LottieLoading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderAdapter = OrderAdapter(requireContext())
        binding.rvOrder.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        orderAdapter.setOnItemClick {
            // to detail activity
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(Constants.SEND_ORDER_TO_DETAIL, it)
            startActivity(intent)
        }

        observableViewModel()
    }

    private fun observableViewModel(){
        val idSeller = prefs.getUser().id
        viewModel.getOrder(idSeller!!).observe(viewLifecycleOwner) {res ->
            when(res.status){
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
                    orderAdapter.submitList(res.data?.data)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}