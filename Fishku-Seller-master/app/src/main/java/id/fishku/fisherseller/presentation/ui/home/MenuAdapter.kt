package id.fishku.fisherseller.presentation.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ItemMenuBinding
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.util.convertCurrencyFormat
import id.fishku.fishersellercore.util.setImage
import id.fishku.fishersellercore.util.toStartCapitalize

/**
 * Menu adapter
 *
 * @property context
 * @constructor Create empty Menu adapter
 */
class MenuAdapter(private val context: Context) : ListAdapter<MenuModel, MenuAdapter.ViewHolder>(differCallback) {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<MenuModel>(){
            override fun areItemsTheSame(oldItem: MenuModel, newItem: MenuModel): Boolean =
                oldItem.id_fish == newItem.id_fish

            override fun areContentsTheSame(oldItem: MenuModel, newItem: MenuModel): Boolean =
                oldItem == newItem

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem, listener, listenerDel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    inner class ViewHolder(private val binding: ItemMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MenuModel, listener: ((MenuModel) -> Unit)?, listenerDel: ((MenuModel) -> Unit)?){
            with(binding){
                imvItem.setImage(id.fishku.fishersellercore.util.Constants.URL_IMAGE+data.photo_url)
                tvName.text = data.name.toStartCapitalize()
                tvPrice.text = context.resources.getString(
                    R.string.price_convert,
                    data.price.convertCurrencyFormat())

                btnEdit.setOnClickListener {
                    listener?.let {
                        listener(data)
                    }
                }
                btnDelete.setOnClickListener {
                    listenerDel?.let {
                        listenerDel(data)
                    }
                }
            }
        }
    }

    private var listener : ((MenuModel) -> Unit)? = null
    fun setOnItemClick(listener: (MenuModel) -> Unit){
        this.listener = listener
    }
    private var listenerDel : ((MenuModel) -> Unit)? = null
    fun setOnDelClick(listener: (MenuModel) -> Unit){
        this.listenerDel = listener
    }
}