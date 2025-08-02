package com.eranio.efficientjanitor.ui.result

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.databinding.TripItemBinding
import com.google.android.material.color.MaterialColors

data class Trip(
    val id: Long,
    val weights: List<Double>
)

private val TRIP_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem == newItem
}

class TripsAdapter : ListAdapter<Trip, TripsAdapter.TripViewHolder>(TRIP_DIFF_CALLBACK) {

    inner class TripViewHolder(private val binding: TripItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            // Title
            val tripText = binding.root.context.getString(R.string.trip_number,adapterPosition+1)
            binding.tripTitle.text = tripText

            // Container for bag icons + weights
            val container = binding.bagContainer
            container.removeAllViews()

            // Common dimensions & tint
            val context = container.context
            val iconSize = context.resources.getDimensionPixelSize(R.dimen.bag_icon_size)
            val margin = context.resources.getDimensionPixelSize(R.dimen.small_margin)
            val tint = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurface)

            // Add one vertical item per bag
            trip.weights.forEach { weight ->
                val itemLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(margin, 0, margin, 0) }
                }

                // Bag icon
                val iv = ImageView(context).apply {
                    setImageResource(R.drawable.bag)
                    imageTintList = ColorStateList.valueOf(tint)
                    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
                }

                // Weight label
                val tv = TextView(context).apply {
                    text = "%.2f kg".format(weight)
                    setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Body1)
                    setTextColor(tint)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = margin / 2 }
                }

                itemLayout.addView(iv)
                itemLayout.addView(tv)
                container.addView(itemLayout)
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
        holder.bind(getItem(position))
    }
}