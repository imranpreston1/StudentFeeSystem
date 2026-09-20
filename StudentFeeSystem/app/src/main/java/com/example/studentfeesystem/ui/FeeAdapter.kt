package com.example.studentfeesystem.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentfeesystem.R
import com.example.studentfeesystem.data.Fee
import com.example.studentfeesystem.databinding.ItemFeeBinding

class FeeAdapter(
    private val onToggleStatus: (Fee) -> Unit,
    private val onPrint: (Fee) -> Unit
) : ListAdapter<Fee, FeeAdapter.FeeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeeViewHolder {
        val binding = ItemFeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeeViewHolder(private val binding: ItemFeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fee: Fee) {
            binding.textMonth.text = fee.month
            binding.textAmount.text = "Amount: Rs. ${fee.amount}"
            binding.textStatus.text = fee.status.uppercase()

            val context = binding.root.context
            val color = if (fee.status == "Paid") {
                ContextCompat.getColor(context, R.color.paid_green)
            } else {
                ContextCompat.getColor(context, R.color.pending_red)
            }
            binding.textStatus.background.setTint(color)

            binding.root.setOnClickListener { onToggleStatus(fee) }
            binding.textPrint.setOnClickListener { onPrint(fee) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Fee>() {
        override fun areItemsTheSame(oldItem: Fee, newItem: Fee) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Fee, newItem: Fee) = oldItem == newItem
    }
}
