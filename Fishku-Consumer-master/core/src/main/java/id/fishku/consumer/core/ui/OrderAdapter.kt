package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemOrderBinding
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.utils.OrderStatus
import id.fishku.consumer.core.utils.convertToRupiah
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onItemOrderClick: (Order) -> Unit,
    private val onTrackOrderClick: (Order) -> Unit,
    private val onInvoiceClick: (Order) -> Unit,
) :
    ListAdapter<Order, OrderAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            if (order.status == OrderStatus.ARRIVE.status) binding.btnTrackOrder.visibility =
                View.GONE
            binding.apply {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("in", "ID"))
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
                val date = parser.parse(order.date)?.let { formatter.format(it) }

                itemTvDateOrder.text = date
                itemTvPriceOrder.text = order.priceTotal.convertToRupiah()
                itemTvStatusOrder.text = order.status
                root.setOnClickListener {
                    onItemOrderClick(order)
                }
                btnTrackOrder.setOnClickListener {
                    onTrackOrderClick(order)
                }
                btnInvoice.setOnClickListener {
                    onInvoiceClick(order)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
                oldItem.orderingID == newItem.orderingID
        }
    }
}