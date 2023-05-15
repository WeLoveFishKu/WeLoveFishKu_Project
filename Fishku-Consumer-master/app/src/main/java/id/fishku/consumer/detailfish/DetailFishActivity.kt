package id.fishku.consumer.detailfish

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.cart.CartActivity
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.loadFishImage
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.ActivityDetailFishBinding
import id.fishku.consumer.livechat.ChatRoomActivity
import id.fishku.consumer.livechat.LiveChatFragment
import id.fishku.consumer.livechat.LiveChatViewModel
import id.fishku.consumer.search.SearchActivity

@AndroidEntryPoint
class DetailFishActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFishBinding
    private val detailFishViewModel: DetailFishViewModel by viewModels()
    private val liveChatViewModel: LiveChatViewModel by viewModels()
    private var consumerID = 0
    private var fishID = 0
    private var maxWeight = 0
    private var sellerEmail: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataFish()
        setupAction()
        getConsumerID()
        inputResult()
        addToBasketHandler()
        setupCart()
        getUserEmail()
        setOnclickToChatRoom()
    }

    private fun getDataFish() {
        val fishID = if (DetailFishActivityArgs.fromBundle(intent.extras as Bundle).fishID != 0) {
            DetailFishActivityArgs.fromBundle(intent.extras as Bundle).fishID
        } else {
            intent.getIntExtra(SearchActivity.EXTRA_FISH_ID, 0)
        }

        detailFishViewModel.getDetailFish(fishID).observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.apply {
                        loadingDetailFish.visibility = View.VISIBLE
                        containerDetailFish.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    this.fishID = fishID
                    it.data?.let { fish ->
                        binding.apply {
                            loadingDetailFish.visibility = View.GONE
                            containerDetailFish.visibility = View.VISIBLE
                        }
                        sellerEmail = fish.email
                        setupData(fish)
                        weightHandler(fish.weight)
                        this.maxWeight = fish.weight
                    }
                }
                is Resource.Error -> {
                    it.message?.showMessage(this)
                    binding.apply {
                        errorDetail.visibility = View.VISIBLE
                        loadingDetailFish.visibility = View.GONE
                        containerDetailFish.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setOnclickToChatRoom(){
        binding.icMessage.setOnClickListener {
            Log.d("TAG", "setOnclickToChatRoom: ")
            setUpToRoomChat()
        }
    }

    private fun getUserEmail(){
        liveChatViewModel.getEmail.observe(this){
            userEmail = it
        }
    }

    private fun setUpToRoomChat(){
        if (userEmail != null && sellerEmail != null){
            val createParams = CreateParams(userEmail!!, sellerEmail!!)

            liveChatViewModel.createConnection(createParams).observe(this){ args ->
                Log.d("TAG", "setOnclickToChatRoom2: $args")
                when(args){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (args.data != null) {
                            Log.d("TAG", "setOnclickToChatRoom3: $args")
                            readMessage(args.data!!.chatId)
                            val intent = Intent(this , ChatRoomActivity::class.java)
                            intent.putExtra(LiveChatFragment.CHAT_KEY, args.data)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Log.d("TAG", "setOnclickToChatRoom4: ${args.message}")
                    }
                }
            }
        }
    }

    private fun readMessage(chatId: String){
        val readParams = userEmail?.let { ReadParams(chatId, it) }
        if (readParams != null) {
            liveChatViewModel.readMessage(readParams)
        }
    }

    private fun setupAction() {
        binding.apply {
            btnAddToCart.setOnClickListener {
                detailFishViewModel.getCurrentCart(this@DetailFishActivity.consumerID)
            }
            toolbarDetail.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            val menuItem = binding.toolbarDetail.menu?.getItem(0)?.actionView
            menuItem?.setOnClickListener { intentToCart() }
        }

    }

    private fun setupData(fish: Fish) {
        binding.apply {
            tvFishNameDetail.text = fish.name
            tvPriceDetail.text = fish.price.convertToRupiah()
            tvSellerName.text = fish.sellerName
            tvAvailableDetail.text = getString(R.string.available_weight, fish.weight.toString())
            tvSellerTpi.text = fish.location
            imgFishDetail.loadFishImage(fish.photoUrl.addPhotoUrl())
        }
    }

    private fun weightHandler(weight: Int) {
        binding.apply {
            btnAddWeight.setOnClickListener {
                var currentWeight = edtWeight.text.toString().toInt()
                if (currentWeight < weight) {
                    currentWeight++
                    edtWeight.setText(currentWeight.toString())
                    if (currentWeight == weight) {
                        btnAddWeight.setImageResource(R.drawable.ic_plus_disable)
                    }

                    if (currentWeight == 2) {
                        btnReduceWeight.setImageResource(R.drawable.ic_minus_enable)
                    }
                }
            }

            btnReduceWeight.setOnClickListener {
                var currentWeight = edtWeight.text.toString().toInt()
                if (currentWeight > 1) {
                    currentWeight--
                    edtWeight.setText(currentWeight.toString())
                    if (currentWeight == 1) {
                        btnReduceWeight.setImageResource(R.drawable.ic_minus_disable)
                    }

                    if (currentWeight == weight - 1) {
                        btnAddWeight.setImageResource(R.drawable.ic_plus_enable)
                    }
                }
            }
        }
    }

    private fun getConsumerID() {
        detailFishViewModel.getId.observe(this) {
            if (it != null) {
                this.consumerID = it
                binding.btnAddToCart.isClickable = true
            }
        }
    }

    private fun addToBasketHandler() {
        detailFishViewModel.currentCart.observe(this) {
            var isAlreadyInCart = false
            val notes = binding.edtNoteDetail.text.toString()
            var weight = binding.edtWeight.text.toString().toInt()
            if (weight > this.maxWeight) {
                weight = maxWeight
                binding.edtWeight.setText(weight.toString())
            }

            when (it) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    val carts = it.data
                    carts?.forEach { cart ->
                        if (cart.fishID == this.fishID) {
                            detailFishViewModel.editCart(cart.cartID, weight + cart.weight)
                            isAlreadyInCart = true
                            return@forEach
                        }
                    }

                    if (!isAlreadyInCart) {
                        detailFishViewModel.inputCart(this.fishID, this.consumerID, notes, weight)
                    }
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun inputResult() {
        detailFishViewModel.inputResult.observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    showCart()
                }
                is Resource.Error -> {
                    showLoading(false)
                    it.message?.showMessage(this)
                }
            }
        }
    }

    private fun setupCart() {
        val menuItem = binding.toolbarDetail.menu?.getItem(0)?.actionView
        val tvAmount = menuItem?.findViewById<TextView>(R.id.cart_badge)
        detailFishViewModel.cart.observe(this) {
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

    private fun showCart() {
        val snackbar = Snackbar
            .make(binding.containerDetailFish,
                getString(R.string.input_cart_message),
                Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.see_cart)) {
                intentToCart()
            }
        snackbar.anchorView = binding.bottomViewDetail
        snackbar.show()
    }

    private fun intentToCart() {
        val cartIntent = Intent(this, CartActivity::class.java)
        startActivity(cartIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingInputCart.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            binding.loadingInputCart.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}