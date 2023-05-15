package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemFishOrderBinding
import id.fishku.consumer.core.domain.model.OrderDetail
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertToKilogram
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.loadFishImage

class FishOrderAdapter : ListAdapter<OrderDetail, FishOrderAdapter.ListViewHolder>(DIFF_CALLBACK) {
    inner class ListViewHolder(private val binding: ItemFishOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderDetail: OrderDetail) {
            binding.apply {
                itemTvNameOrder.text = orderDetail.fishName
                itemTvWeightOrder.text = orderDetail.weight.convertToKilogram()
                itemTvPriceOrder.text = orderDetail.fishPrice.convertToRupiah()
                orderDetail.photoUrl?.addPhotoUrl()?.let { itemImgPhotoOrder.loadFishImage(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemFishOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val cart = getItem(position)
        holder.bind(cart)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderDetail>() {
            override fun areItemsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean =
                oldItem == newItem
        }
    }
}