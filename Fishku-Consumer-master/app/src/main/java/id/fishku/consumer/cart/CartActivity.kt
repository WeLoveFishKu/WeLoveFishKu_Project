package id.fishku.consumer.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.checkout.CheckoutActivity
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.ui.CartAdapter
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.ActivityCartBinding
import id.fishku.consumer.detailfish.DetailFishActivity
import id.fishku.consumer.invoice.InvoiceActivity
import id.fishku.consumer.search.SearchActivity

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val cartAdapter: CartAdapter by lazy { CartAdapter(::cartItemClicked) }
    private val carts = arrayListOf<Cart>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupData()
        setupAction()
        editResult()
        deleteResult()
    }

    private fun setupData() {
        cartViewModel.cart.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.apply {
                        rvCartProduct.visibility = View.GONE
                        loadingCart.visibility = View.VISIBLE
                        imgErrorCart.visibility = View.GONE
                        tvErrorCart.visibility = View.GONE
                        bottomViewBuy.visibility = View.GONE
                    }
                }

                is Resource.Success -> {
                    binding.loadingCart.visibility = View.GONE
                    if (it.data?.isNotEmpty() == true) {
                        binding.rvCartProduct.visibility = View.VISIBLE
                        binding.bottomViewBuy.visibility = View.VISIBLE

                        cartAdapter.submitList(it.data)
                        setupAdapter()
                    } else {
                        binding.apply {
                            imgEmptyCart.visibility = View.VISIBLE
                            tvEmptyCart.visibility = View.VISIBLE
                        }
                    }
                }

                is Resource.Error -> {
                    binding.apply {
                        loadingCart.visibility = View.GONE
                        imgErrorCart.visibility = View.VISIBLE
                        tvErrorCart.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        binding.rvCartProduct.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            setHasFixedSize(true)
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            dividerItemDecoration.setDrawable(getDrawable(R.drawable.bg_divider)!!)
            addItemDecoration(dividerItemDecoration)
            adapter = cartAdapter
        }
    }

    private fun setupAction() {
        binding.toolbarCart.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnBuyCart.setOnClickListener {
            if (this.carts.isEmpty()) {
                getString(R.string.select_product).showMessage(this)
            } else {
                val checkoutIntent = Intent(this, CheckoutActivity::class.java)
                checkoutIntent.putParcelableArrayListExtra(CheckoutActivity.EXTRA_CARTS, this.carts)
                startActivity(checkoutIntent)
            }
        }

        cartAdapter.setOnItemClickCallback(object : CartAdapter.OnItemClickCallback {
            override fun onDeleteClicked(cart: Cart, position: Int) {
                cartViewModel.deleteCart(cart.cartID, position)
            }

            override fun onSelectClicked(cart: Cart, isChecked: Boolean) {
                if (isChecked) {
                    this@CartActivity.carts.add(cart)
                } else {
                    this@CartActivity.carts.remove(cart)
                }
                if (carts.isNotEmpty()) {
                    binding.tvPriceTotalCart.text =
                        carts.sumOf { it.weight * it.price }.convertToRupiah()
                } else {
                    binding.tvPriceTotalCart.text = getString(R.string.no_price)
                }
            }

        })
    }

    private fun editResult() {
        cartViewModel.editResult.observe(this) {
            when (it) {
                is Resource.Loading -> {}

                is Resource.Success -> {}

                is Resource.Error -> {
                    binding.apply {
                        btnBuyCart.visibility = View.GONE
                        rvCartProduct.visibility = View.GONE
                        imgErrorCart.visibility = View.VISIBLE
                        tvErrorCart.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun deleteResult() {
        cartViewModel.deleteResult.observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> showLoading(false)
                is Resource.Error -> {
                    binding.apply {
                        btnBuyCart.visibility = View.GONE
                        rvCartProduct.visibility = View.GONE
                        imgErrorCart.visibility = View.VISIBLE
                        tvErrorCart.visibility = View.VISIBLE
                    }
                    showLoading(false)
                    it.message?.showMessage(this)
                }
            }
        }

        cartViewModel.deletedPosition.observe(this) { position ->
            if (cartAdapter.currentList.size == 1) {
                binding.apply {
                    rvCartProduct.visibility = View.GONE
                    bottomViewBuy.visibility = View.GONE
                    imgEmptyCart.visibility = View.VISIBLE
                    tvEmptyCart.visibility = View.VISIBLE
                }
            }
            cartAdapter.deleteItem(position)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingDeleteCart.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            binding.loadingDeleteCart.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun cartItemClicked(cart: Cart) {
        val detailIntent = Intent(this, DetailFishActivity::class.java)
        detailIntent.putExtra(SearchActivity.EXTRA_FISH_ID, cart.fishID)
        startActivity(detailIntent)
    }
}