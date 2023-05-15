package id.fishku.fisherseller.presentation.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ItemOrderBinding
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.util.convertCurrencyFormat
import id.fishku.fishersellercore.util.convertDate

/**
 * Order adapter
 *
 * @property context
 * @constructor Create empty Order adapter
 */
class OrderAdapter(private val context: Context) : ListAdapter<OrderModel, OrderAdapter.ViewHolder>(differCallback) {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<OrderModel>(){
            override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean =
                oldItem.id_order == newItem.id_order

            override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean =
                oldItem == newItem

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    inner class ViewHolder(private val binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderModel, listener: ((OrderModel) -> Unit)?){
            with(binding){
                tvDate.text = data.date.convertDate(context)
                tvPrice.text = context.resources.getString(R.string.price_rp, data.total_price.convertCurrencyFormat())
                tvStatus.text = data.status

                btnStatus.setOnClickListener {
                    listener?.let {
                        listener(data)
                    }
                }
            }
        }
    }

    private var listener : ((OrderModel) -> Unit)? = null
    fun setOnItemClick(listener: (OrderModel) -> Unit){
        this.listener = listener
    }
}