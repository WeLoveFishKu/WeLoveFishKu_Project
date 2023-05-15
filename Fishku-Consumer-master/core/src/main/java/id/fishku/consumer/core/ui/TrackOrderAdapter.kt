package id.fishku.consumer.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import id.fishku.consumer.core.R
import id.fishku.consumer.core.databinding.ItemDeliveryBinding
import id.fishku.consumer.core.domain.model.Track

class TrackOrderAdapter : ListAdapter<Track, TrackOrderAdapter.ListViewHolder>(DIFF_CALLBACK) {
    inner class ListViewHolder(private val binding: ItemDeliveryBinding, viewType: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.apply {
                itemTvTitleTrack.text = track.title
                itemTvContentTrack.text = track.content
                itemTvDateTrack.text = track.date
            }

            when {
                track.isActive -> binding.timeline.marker =
                    itemView.context.getDrawable(R.drawable.ic_marker_active)
                else -> binding.timeline.marker =
                    itemView.context.getDrawable(R.drawable.ic_marker_inactive)
            }
        }

        init {
            binding.timeline.initLine(viewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding, viewType)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
                oldItem.date == newItem.date
        }
    }
}