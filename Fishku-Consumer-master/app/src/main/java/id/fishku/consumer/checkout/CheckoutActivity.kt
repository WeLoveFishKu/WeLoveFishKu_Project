package id.fishku.consumer.checkout
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.DataNotification
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.ui.CheckoutAdapter
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.parcelableArrayList
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.ActivityCheckoutBinding
import id.fishku.consumer.invoice.InvoiceActivity
import id.fishku.consumer.main.MainActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private val checkoutAdapter: CheckoutAdapter by lazy { CheckoutAdapter() }
    private lateinit var binding: ActivityCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private var consumerID = 0
    private var payerEmail = ""
    private var invoiceUrl = ""
    private var address = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var userName = ""

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val address = result.data?.getStringExtra(AddressActivity.EXTRA_ADDRESS)
                val latitude = result.data?.getDoubleExtra(AddressActivity.EXTRA_LATITUDE, 0.0)
                val longitude = result.data?.getDoubleExtra(AddressActivity.EXTRA_LONGITUDE, 0.0)
                binding.tvAddressCheckout.apply {
                    text = address
                    visibility = View.VISIBLE
                }
                this.address = address ?: ""
                this.latitude = latitude ?: 0.0
                this.longitude = longitude ?: 0.0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUser()
        setupAction()
        setupProducts()
        setupPrice()
        inputOrderResult()
        createInvoiceResult()
        inputDetailOrderResult()
    }

    private fun setupUser() {
        checkoutViewModel.apply {
            getEmail.observe(this@CheckoutActivity) {
                this@CheckoutActivity.payerEmail = it
            }
            getAddress.observe(this@CheckoutActivity) {
                this@CheckoutActivity.address = it
            }
            getId.observe(this@CheckoutActivity) {
                this@CheckoutActivity.consumerID = it
            }
        }
    }

    private fun setupAction() {
        binding.toolbarCheckout.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnGetAddress.setOnClickListener {
            val intentAddress = Intent(this, AddressActivity::class.java)
            resultLauncher.launch(intentAddress)
        }
        binding.btnOrder.setOnClickListener { orderHandler() }
    }

    private fun setupPrice() {
        val products = intent.parcelableArrayList<Cart>(EXTRA_CARTS)
        val subTotalProduct = products?.sumOf { it.price * it.weight }
        val subTotalDelivery = 25_000
        binding.apply {
            tvSubtotalProduct.text = subTotalProduct?.convertToRupiah()
            tvSubtotalDelivery.text = subTotalDelivery.convertToRupiah()
            tvTotalCheckout.text = subTotalProduct?.plus(subTotalDelivery)?.convertToRupiah()
        }
    }

    private fun orderHandler() {
        val subTotalDelivery = 25_000
        val carts = intent.parcelableArrayList<Cart>(EXTRA_CARTS)
        val amount = carts?.sumOf { it.price * it.weight }?.plus(subTotalDelivery)
        val selectedCourierId = binding.rgCourier.checkedRadioButtonId

        if (selectedCourierId <= 0) {
            getString(R.string.choose_courier).showMessage(this)
        } else {
            if (amount != null) {
                checkoutViewModel.createInvoice(this.consumerID, this.payerEmail, amount)

            }
        }
    }

    private fun sendNotifOrder(){
        val carts = intent.parcelableArrayList<Cart>(EXTRA_CARTS)
        carts?.forEach { cart ->
            lifecycleScope.launch {
                checkoutViewModel.getListenerToken(cart.sellerEmail).collect{token ->
                    val notification = NotificationRequest(
                        data = DataNotification(
                            title =  userName,
                            content = "Ingin memsan ${cart.fishName}",
                        ),
                        registration_ids = listOf(token)
                    )
                    checkoutViewModel.pushNotification(notification)
                }
            }


        }

    }

    private fun createInvoiceResult() {
        checkoutViewModel.resultInvoice.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.loadingCheckout.visibility = View.VISIBLE
                    binding.btnOrder.isEnabled = false
                }

                is Resource.Success -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true

                    val notes = binding.edtNoteCheckout.text.toString()
                    val selectedCourierId = binding.rgCourier.checkedRadioButtonId
                    val rbCourier = findViewById<RadioButton>(selectedCourierId)


                    val invoiceUrl = result.data?.invoiceUrl
                    if (invoiceUrl != null) {
                        this.invoiceUrl = invoiceUrl
                        checkoutViewModel.inputOrder(
                            notes,
                            rbCourier.text.toString(),
                            this.address,
                            invoiceUrl,
                            this.latitude,
                            this.longitude
                        )
                    }
                }

                is Resource.Error -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true
                    result.message?.showMessage(this)
                }
            }
        }
    }

    private fun inputOrderResult() {
        val carts = intent.parcelableArrayList<Cart>(EXTRA_CARTS)

        checkoutViewModel.resultOrderingID.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.loadingCheckout.visibility = View.VISIBLE
                    binding.btnOrder.isEnabled = false
                }

                is Resource.Success -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true

                    val orderingID = result.data?.orderingID
                    if (carts != null && orderingID != null) {
                        checkoutViewModel.inputOrderDetail(this.consumerID, carts, orderingID)
                    }
                }

                is Resource.Error -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true

                    result.message?.showMessage(this)
                }
            }
        }
    }

    private fun inputDetailOrderResult() {
        checkoutViewModel.resultInputDetailOrder.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.loadingCheckout.visibility = View.VISIBLE
                    binding.btnOrder.isEnabled = false
                }

                is Resource.Success -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true

                    sendNotifOrder()

                    val intentMain = Intent(this, MainActivity::class.java)
                    intentMain.putExtra(InvoiceActivity.EXTRA_URL, this.invoiceUrl)
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentMain)
                    finish()
                }

                is Resource.Error -> {
                    binding.loadingCheckout.visibility = View.GONE
                    binding.btnOrder.isEnabled = true

                    result.message?.showMessage(this)
                }
            }
        }
    }

    private fun setupProducts() {
        val products = intent.parcelableArrayList<Cart>(EXTRA_CARTS)

        checkoutAdapter.submitList(products)
        binding.rvOrderCheckout.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            setHasFixedSize(true)
            adapter = checkoutAdapter
        }
    }

    companion object {
        const val EXTRA_CARTS = "id.fishku.consumer.checkout.extra_carts"
    }
}