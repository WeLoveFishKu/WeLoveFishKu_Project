package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemFilterListBinding
import id.fishku.consumer.core.domain.model.Filter

class FilterAdapter(private val onItemClick: (Filter) -> Unit) :
    ListAdapter<Filter, FilterAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(private val binding: ItemFilterListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(filter: Filter) {
            binding.apply {
                textView.text = filter.filterName
                root.setOnClickListener {
                    onItemClick(filter)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemFilterListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val filter = getItem(position)
        holder.bind(filter)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Filter>() {
            override fun areItemsTheSame(oldItem: Filter, newItem: Filter): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Filter, newItem: Filter): Boolean =
                oldItem.filterID == newItem.filterID
        }
    }
}