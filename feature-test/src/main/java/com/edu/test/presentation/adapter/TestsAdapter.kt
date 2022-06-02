package com.edu.test.presentation.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.common.presentation.model.TestModel
import com.edu.common.presentation.model.TestStatusEnum
import com.edu.common.utils.getDateAndTime
import com.edu.test.R
import com.edu.test.databinding.ItemTestBinding
import java.text.SimpleDateFormat
import java.util.*

class TestsAdapter(
    private val listener: ItemClickListener,
    private val isUserAdmin: Boolean = false
) :
    ListAdapter<TestModel, TestsAdapter.TestViewHolder>(ItemCallback) {


    var tracker: SelectionTracker<String>? = null

    inner class TestViewHolder(private val binding: ItemTestBinding) :
        BaseViewHolderForSelection(binding.root) {
        override fun bind(position: Int) {
            getItem(position).apply {
                binding.root.setOnClickListener {
                    listener.onTestClick(this)
                }
                binding.testTitle.text = title
                if (!isUserAdmin) {
                    binding.statusTextView.text = status.status
                    if (status == TestStatusEnum.IN_PROGRESS) {
                        binding.statusTextView.setBackgroundColor(
                            binding.root.resources.getColor(
                                R.color.green
                            )
                        )
                    }
                } else {
                    binding.statusTextView.isVisible = false
                }

                binding.dateTime.text = date.getDateAndTime()

                tracker?.let {
                    if (it.isSelected(getItem(position).uid)) {
                        binding.root.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.selected_item_color
                            )
                        )
                    } else {
                        binding.root.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.white
                            )
                        )
                    }

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

    object ItemCallback : DiffUtil.ItemCallback<TestModel>() {
        override fun areItemsTheSame(oldItem: TestModel, newItem: TestModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: TestModel,
            newItem: TestModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    interface ItemClickListener {
        fun onTestClick(model: TestModel)
    }
}