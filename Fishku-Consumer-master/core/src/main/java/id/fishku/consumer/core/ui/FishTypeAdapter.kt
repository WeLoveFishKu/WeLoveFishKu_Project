package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.fishku.consumer.core.databinding.ItemFishTypeBinding
import id.fishku.consumer.core.domain.model.FishType
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertFishName
import id.fishku.consumer.core.utils.loadFishImage

class FishTypeAdapter(private val onItemClick: (FishType) -> Unit) :
    ListAdapter<FishType, FishTypeAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(private val binding: ItemFishTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fish: FishType) {
            binding.apply {
                itemTvName.text = fish.name.convertFishName()
                itemImgPhoto.loadFishImage(fish.photoUrl.addPhotoUrl())
                root.setOnClickListener {
                    onItemClick(fish)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemFishTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val fish = getItem(position)
        holder.bind(fish)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FishType>() {
            override fun areItemsTheSame(oldItem: FishType, newItem: FishType): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: FishType, newItem: FishType): Boolean =
                oldItem.fishID == newItem.fishID
        }
    }
}
