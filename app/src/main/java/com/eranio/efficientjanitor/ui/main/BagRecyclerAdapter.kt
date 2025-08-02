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
import com.eranio.efficientjanitor.data.local.BagEntity
import com.eranio.efficientjanitor.databinding.BagItemBinding
import com.eranio.efficientjanitor.util.formatKg

class BagRecyclerAdapter(
    private val onDeleteClicked: (BagEntity) -> Unit
) : ListAdapter<BagEntity, BagRecyclerAdapter.BagViewHolder>(BagDiffCallback()) {

    inner class BagViewHolder(private val binding: BagItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bag: BagEntity) {
            // make sure it's visible again in case it was hidden
            binding.root.visibility = View.VISIBLE

            // show weight
            binding.bagWeightTextView.text = bag.weight.formatKg()

            // delete animation + callback
            binding.deleteButton.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.item_slide_out)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.root.visibility = View.INVISIBLE
                        onDeleteClicked(bag)
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

        // entry animation
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_slide_in)
        holder.itemView.startAnimation(animation)
    }

    private class BagDiffCallback : DiffUtil.ItemCallback<BagEntity>() {
        override fun areItemsTheSame(old: BagEntity, new: BagEntity): Boolean =
            old.id == new.id

        override fun areContentsTheSame(old: BagEntity, new: BagEntity): Boolean =
            old == new
    }
}
