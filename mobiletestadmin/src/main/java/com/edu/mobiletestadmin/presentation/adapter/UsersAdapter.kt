package com.edu.mobiletestadmin.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edu.mobiletestadmin.R
import com.edu.mobiletestadmin.databinding.ItemUserBinding
import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.utils.IImageLoader

class UsersAdapter(private val listener: Listener, private val imageLoader: IImageLoader) :
    ListAdapter<User, UsersAdapter.UserVH>(object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }) {

    var tracker: SelectionTracker<String>? = null


    inner class UserVH(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            getItem(position).apply {
                imageLoader.loadImageWithCircleShape(
                    binding.image,
                    avatar,
                    R.drawable.ic_user_placeholder
                )
                binding.username.text = name
                binding.root.setOnClickListener {
                    listener.onUserClicked(this)
                }
            }

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

        fun getItem(): ItemDetailsLookup.ItemDetails<String> =

            //1
            object : ItemDetailsLookup.ItemDetails<String>() {

                //2
                override fun getPosition(): Int = adapterPosition

                //3
                override fun getSelectionKey(): String = getItem(adapterPosition).uid
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        return UserVH(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        holder.bind(position)
    }

    interface Listener {
        fun onUserClicked(user: User)
    }
}