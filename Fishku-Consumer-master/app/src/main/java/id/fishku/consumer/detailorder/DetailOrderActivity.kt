package id.fishku.consumer.detailorder

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.ui.FishOrderAdapter
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.parcelable
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.ActivityDetailOrderBinding

@AndroidEntryPoint
class DetailOrderActivity : AppCompatActivity() {

    private val fishOrderAdapter: FishOrderAdapter by lazy { FishOrderAdapter() }
    private lateinit var binding: ActivityDetailOrderBinding
    private val detailOrderViewModel: DetailOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupProducts()
        setupData()
    }

    private fun setupToolbar() {
        binding.toolbarDetailOrder.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupProducts() {
        binding.rvDetailOrder.apply {
            layoutManager = LinearLayoutManager(this@DetailOrderActivity)
            setHasFixedSize(true)
            adapter = fishOrderAdapter
        }
    }

    private fun setupData() {

        val order = intent.parcelable<Order>(ORDER_ITEM)
        if (order != null) {
            detailOrderViewModel.orderDetail(order.orderingID).observe(this) {
                when (it) {
                    is Resource.Loading -> {
                        binding.apply {
                            containerDetailOrder.visibility = View.GONE
                            loadingDetailOrder.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Success -> {
                        binding.apply {
                            containerDetailOrder.visibility = View.VISIBLE
                            loadingDetailOrder.visibility = View.GONE
                        }
                        fishOrderAdapter.submitList(it.data)
                        setupProducts()
                    }
                    is Resource.Error -> {
                        binding.apply {
                            loadingDetailOrder.visibility = View.GONE
                            tvErrorDetailOrder.visibility = View.VISIBLE
                        }

                        it.message?.showMessage(this)
                    }
                }
            }
        }

        binding.apply {
            tvSubtotalProdukOrder.text = order?.priceTotal?.convertToRupiah()
            tvTotalPriceOrder.text = order?.priceTotal?.convertToRupiah()
        }
    }

    companion object {
        const val ORDER_ITEM = "id.fishku.consumer.detailorder.order_item"
    }
}