package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemCartBinding
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.utils.convertToKilogram
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.loadFishImage

class CartAdapter(private val onItemClick: (Cart) -> Unit) :
    ListAdapter<Cart, CartAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun deleteItem(position: Int) {
        val carts = arrayListOf<Cart>()
        carts.addAll(currentList)
        carts.removeAt(position)
        submitList(carts)
    }

    inner class ListViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.apply {
                itemTvNameCart.text = cart.fishName
                itemTvPriceCart.text = cart.price.convertToRupiah()
                itemImgPhotoCart.loadFishImage(cart.photoUrl.addPhotoUrl())
                itemTvWeightCart.text = cart.weight.convertToKilogram()
                itemImgPhotoCart.loadFishImage(cart.photoUrl)
                root.setOnClickListener {
                    onItemClick(cart)
                }

                btnDeleteCart.setOnClickListener {
                    onItemClickCallback.onDeleteClicked(cart, absoluteAdapterPosition)
                }

                itemCheckboxCart.setOnCheckedChangeListener { _, isChecked ->
                    onItemClickCallback.onSelectClicked(cart, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val cart = getItem(position)
        holder.bind(cart)
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(cart: Cart, position: Int)
        fun onSelectClicked(cart: Cart, isChecked: Boolean)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean =
                oldItem.cartID == newItem.cartID
        }
    }
}