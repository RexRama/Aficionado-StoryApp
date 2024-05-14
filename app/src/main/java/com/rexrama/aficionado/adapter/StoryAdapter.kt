package com.rexrama.aficionado.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ItemListStoriesBinding

class StoryAdapter(private var storyItem: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    private var onItemClickCallBack: OnItemClickCallBack? = null


    class ListViewHolder(var binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return storyItem.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyItem = storyItem[position]
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

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }


    interface OnItemClickCallBack {
        fun onItemClicked(data: ListStoryItem)
    }
}