package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.edu.common.utils.getDateAndTime
import com.edu.test.R
import com.edu.test.databinding.ItemTestBinding
import com.edu.test.domain.model.dbModels.TestDomain

class UnPublishedTestsAdapter(private val listener: Listener) :
    ListAdapter<TestDomain, UnPublishedTestsAdapter.UnPublishedVH>(
        object : DiffUtil.ItemCallback<TestDomain>() {
            override fun areItemsTheSame(oldItem: TestDomain, newItem: TestDomain): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: TestDomain, newItem: TestDomain): Boolean {
                return oldItem == newItem
            }

        }
    ) {

    var tracker: SelectionTracker<String>? = null

    inner class UnPublishedVH(val binding: ItemTestBinding) :
        BaseViewHolderForSelection(binding.root) {
        override fun bind(position: Int) {
            with(getItem(position)) {
                binding.statusTextView.isVisible = false
                binding.dateTime.text = date.getDateAndTime()
                binding.testTitle.text = name
                binding.root.setOnClickListener {
                    listener.onTestClick(this)
                }
                tracker?.let { tracker ->
                    if (tracker.isSelected(uid))
                        binding.root.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.selected_item_color
                            )
                        )
                    else binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            android.R.color.transparent
                        )
                    )
                }
            }
        }

        override fun getStringDetails(): ItemDetailsLookup.ItemDetails<String> {
            return object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): String {
                    return getItem(adapterPosition).uid

                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnPublishedVH {
        return UnPublishedVH(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UnPublishedVH, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onTestClick(test: TestDomain)
    }

}