package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemOrderCheckoutBinding
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertToKilogram
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.loadFishImage

class CheckoutAdapter : ListAdapter<Cart, CheckoutAdapter.ListViewHolder>(DIFF_CALLBACK) {
    inner class ListViewHolder(private val binding: ItemOrderCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.apply {
                itemTvNameCheckout.text = cart.fishName
                itemTvWeightCheckout.text = cart.weight.convertToKilogram()
                itemTvPriceCheckout.text = cart.price.convertToRupiah()
                itemImgPhotoCheckout.loadFishImage(cart.photoUrl.addPhotoUrl())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemOrderCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val cart = getItem(position)
        holder.bind(cart)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean =
                oldItem.cartID == newItem.cartID
        }
    }
}