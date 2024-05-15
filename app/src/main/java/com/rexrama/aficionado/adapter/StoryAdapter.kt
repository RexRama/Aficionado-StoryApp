package com.rexrama.aficionado.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ItemListStoriesBinding

class StoryAdapter :PagingDataAdapter<ListStoryItem,StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallBack: OnItemClickCallBack? = null

    class ListViewHolder(var binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            val storyDate = storyItem.createdAt.take(10)
            Glide.with(holder.itemView.context)
                .load(storyItem.photoUrl)
                .into(holder.binding.ivStoryImage)
            holder.apply {
                binding.tvStoryUsername.text = storyItem.name
                binding.tvStoryDate.text = storyDate
            }


            holder.itemView.setOnClickListener {
                onItemClickCallBack?.onItemClicked(storyItem)
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }


    interface OnItemClickCallBack {
        fun onItemClicked(data: ListStoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}