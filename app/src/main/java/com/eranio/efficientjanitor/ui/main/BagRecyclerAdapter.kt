package com.eranio.efficientjanitor.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.databinding.BagItemBinding
import com.eranio.efficientjanitor.formatKg

class BagRecyclerAdapter(
    private val onDeleteClicked: (Double) -> Unit
) : ListAdapter<Double, BagRecyclerAdapter.BagViewHolder>(BagDiffCallback()) {

    inner class BagViewHolder(val binding: BagItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weight: Double) {
            binding.root.visibility = View.VISIBLE
            binding.bagWeightTextView.text = weight.formatKg()
            binding.deleteButton.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.item_slide_out)

                // animate deletion
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.root.visibility = View.INVISIBLE
                        onDeleteClicked(weight)
                    }
                })
                binding.root.startAnimation(animation)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BagViewHolder {
        val binding = BagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BagViewHolder, position: Int) {
        holder.bind(getItem(position))

        // animate entry
        val context = holder.itemView.context
        val animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_in)
        holder.itemView.startAnimation(animation)
    }
}

class BagDiffCallback : DiffUtil.ItemCallback<Double>() {
    override fun areItemsTheSame(oldItem: Double, newItem: Double): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: Double, newItem: Double): Boolean = oldItem == newItem
}