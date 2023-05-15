package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemFishProductGridBinding
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertToRupiah
import id.fishku.consumer.core.utils.loadFishImage

class SearchAdapter(private val onItemClick: (Fish) -> Unit) :
    ListAdapter<Fish, SearchAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(private val binding: ItemFishProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fish: Fish) {
            binding.apply {
                itemTvName.text = fish.name
                itemTvPrice.text = fish.price.convertToRupiah()
                itemImgPhoto.loadFishImage(fish.photoUrl.addPhotoUrl())
                root.setOnClickListener {
                    onItemClick(fish)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemFishProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val fish = getItem(position)
        holder.bind(fish)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Fish>() {
            override fun areItemsTheSame(oldItem: Fish, newItem: Fish): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: Fish, newItem: Fish): Boolean =
                oldItem.fishID == newItem.fishID
        }
    }
}