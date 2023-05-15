package id.fishku.fisherseller.presentation.ui.order.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ItemMenuOrderBinding
import id.fishku.fishersellercore.model.DetailOrderModel
import id.fishku.fishersellercore.util.Constants
import id.fishku.fishersellercore.util.convertCurrencyFormat
import id.fishku.fishersellercore.util.setImage
import id.fishku.fishersellercore.util.toStartCapitalize

/**
 * Detail adapter
 *
 * @property context
 * @constructor Create empty Detail adapter
 */
class DetailAdapter(private val context: Context) : ListAdapter<DetailOrderModel, DetailAdapter.ViewHolder>(differCallback) {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<DetailOrderModel>(){
            override fun areItemsTheSame(oldItem: DetailOrderModel, newItem: DetailOrderModel): Boolean =
                oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: DetailOrderModel, newItem: DetailOrderModel): Boolean =
                oldItem == newItem

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    inner class ViewHolder(private val binding: ItemMenuOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailOrderModel){
            with(binding){
                imvItem.setImage(Constants.URL_IMAGE)
                tvName.text = data.fish_name.toStartCapitalize()
                tvWeight.text = data.weight
                tvPrice.text = context.resources.getString(
                    R.string.price_convert_order,
                    data.price.convertCurrencyFormat())
            }
        }
    }
}