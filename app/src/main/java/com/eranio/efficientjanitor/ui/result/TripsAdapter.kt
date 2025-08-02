package com.eranio.efficientjanitor.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.databinding.TripItemBinding

class TripsAdapter : ListAdapter<List<Double>, TripsAdapter.TripViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<List<Double>>() {
        override fun areItemsTheSame(oldItem: List<Double>, newItem: List<Double>) =
            oldItem === newItem  // identity, or compare some stable ID if you have one

        override fun areContentsTheSame(oldItem: List<Double>, newItem: List<Double>) =
            oldItem == newItem
    }

    inner class TripViewHolder(private val binding: TripItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: List<Double>, position: Int) {
            // 1) Title and details
            binding.tripTitle.text = "Trip ${position + 1}"
            binding.tripWeights.text = trip.joinToString(", ") { "%.2f kg".format(it) }

            // 2) Icon container
            val container = binding.bagIconsContainer
            container.removeAllViews()

            val context = container.context
            val iconSize = context.resources.getDimensionPixelSize(R.dimen.bag_icon_size)
            val iconMargin = context.resources.getDimensionPixelSize(R.dimen.small_margin)

            trip.forEach { _ ->
                val iv = ImageView(context).apply {
                    setImageResource(R.drawable.bag)
                    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                        rightMargin = iconMargin
                    }
                }
                container.addView(iv)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}
