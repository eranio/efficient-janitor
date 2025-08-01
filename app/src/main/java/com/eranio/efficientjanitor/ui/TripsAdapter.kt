package com.eranio.efficientjanitor.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eranio.efficientjanitor.databinding.TripItemBinding

class TripsAdapter : ListAdapter<List<Double>, TripsAdapter.TripViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<List<Double>>() {
        override fun areItemsTheSame(oldItem: List<Double>, newItem: List<Double>) = oldItem == newItem
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: List<Double>, newItem: List<Double>) = oldItem == newItem
    }

    inner class TripViewHolder(private val binding: TripItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: List<Double>, position: Int) {
            binding.tripTitle.text = "Trip ${position + 1}"
            binding.tripDetails.text = trip.joinToString(", ") { "%.2f kg".format(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}
