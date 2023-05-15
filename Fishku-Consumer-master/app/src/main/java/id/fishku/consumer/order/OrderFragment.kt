package id.fishku.consumer.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.ui.OrderAdapter
import id.fishku.consumer.core.utils.DataMapper
import id.fishku.consumer.core.utils.OrderStatus
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentOrderBinding
import id.fishku.consumer.detailorder.DetailOrderActivity
import id.fishku.consumer.invoice.InvoiceActivity
import id.fishku.consumer.statusdelivery.StatusDeliveryActivity

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding
    private val orderAdapter: OrderAdapter by lazy {
        OrderAdapter(
            ::itemOrderClicked,
            ::trackOrderClicked,
            ::invoiceClicked
        )
    }
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupAdapter()
    }

    private fun setupData() {
        val position = arguments?.getInt(SECTION_NUMBER)
        orderViewModel.orders.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding?.apply {
                        loadingOrder.visibility = View.VISIBLE
                        rvOrder.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    binding?.loadingOrder?.visibility = View.GONE
                    val dataOrders = DataMapper.filterOrder(it.data)
                    if (position == 1) {
                        val orders =
                            dataOrders.filter { order -> order.status == OrderStatus.WAITING.status || order.status == OrderStatus.SENDING.status }
                        if (orders.isEmpty()) {
                            binding?.tvEmptyOrder?.visibility = View.VISIBLE
                        } else {
                            binding?.rvOrder?.visibility = View.VISIBLE
                            orderAdapter.submitList(orders)
                            setupAdapter()
                        }
                    } else {
                        val orders =
                            dataOrders.filter { order -> order.status == OrderStatus.ARRIVE.status }
                        orderAdapter.submitList(orders)
                        if (orders.isEmpty()) {
                            binding?.tvEmptyOrder?.visibility = View.VISIBLE
                        } else {
                            binding?.rvOrder?.visibility = View.VISIBLE
                            orderAdapter.submitList(orders)
                            setupAdapter()
                        }
                    }
                }
                is Resource.Error -> {
                    binding?.loadingOrder?.visibility = View.GONE
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    private fun setupAdapter() {
        binding?.rvOrder?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
            adapter = orderAdapter
        }
    }

    private fun itemOrderClicked(order: Order) {
        val detailOrderIntent = Intent(requireContext(), DetailOrderActivity::class.java)
        detailOrderIntent.putExtra(DetailOrderActivity.ORDER_ITEM, order)
        startActivity(detailOrderIntent)
    }

    private fun trackOrderClicked(order: Order) {
        val trackOrderIntent = Intent(requireContext(), StatusDeliveryActivity::class.java)
        startActivity(trackOrderIntent)
    }

    private fun invoiceClicked(order: Order) {
        val intentMain = Intent(requireContext(), InvoiceActivity::class.java)
        intentMain.putExtra(InvoiceActivity.EXTRA_URL, order.invoiceUrl)
        startActivity(intentMain)
    }

    companion object {
        const val SECTION_NUMBER = "id.fishku.consumer.order.section_number"
    }
}