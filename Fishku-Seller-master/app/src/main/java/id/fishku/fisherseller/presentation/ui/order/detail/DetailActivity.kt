package id.fishku.fisherseller.presentation.ui.order.detail

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityDetailBinding
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.DashboardActivity
import id.fishku.fishersellercore.model.DetailOrderModel
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.util.Constants

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityDetailBinding? = null

    private val binding get() = _binding!!
    private val viewModel: DetailOrderViewModel by viewModels()
    private lateinit var detailAdapter: DetailAdapter
    private var _order: OrderModel? = null
    private val order get() = _order!!
    private var address = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val address = result.data?.getStringExtra(AddressActivity.EXTRA_ADDRESS)
                val latitude = result.data?.getDoubleExtra(AddressActivity.EXTRA_LATITUDE, 0.0)
                val longitude = result.data?.getDoubleExtra(AddressActivity.EXTRA_LONGITUDE, 0.0)

                binding.tvAddress.apply {
                    text = address
                    visibility = View.VISIBLE
                }

                this.address = address ?: ""
                this.latitude = latitude ?: 0.0
                this.longitude = longitude ?: 0.0
            }
        }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener(this)

        setupAction()

        _order = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Constants.SEND_ORDER_TO_DETAIL, OrderModel::class.java)
        } else {
            intent.getParcelableExtra<OrderModel>(Constants.SEND_ORDER_TO_DETAIL) as OrderModel
        }

        detailAdapter = DetailAdapter(applicationContext)
        binding.rvOrder.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }

        sendRequest(order.id_order)
    }

    private fun setupAction() {
        binding.btnGetAddress.setOnClickListener {
            val intentAddress = Intent(this, AddressActivity::class.java)
            resultLauncher.launch(intentAddress)
        }
    }

    private fun sendRequest(idOrder: String) {
        observableViewModel(idOrder)
    }

    private fun observableViewModel(idOrder: String) {
        viewModel.getDetailOrder(idOrder).observe(this) { res ->
            when (res.status) {
                Status.LOADING -> {

                }
                Status.ERROR -> {

                }
                Status.SUCCESS -> {
                    detailAdapter.submitList(res.data?.data)
                    moreDetail(res.data?.data?.get(0))
                }
            }
        }
    }

    private fun moreDetail(data: DetailOrderModel?) {
        binding.tvAddress.text = data?.address
        binding.tvSub.text = data?.fish_price
        binding.tvNote.text = data?.note_order
        binding.tvTotal.text = data?.price
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _order = null
        _binding = null
    }
}