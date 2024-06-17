package com.rexrama.aficionado.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rexrama.aficionado.adapter.StoryAdapter.Companion.DIFF_CALLBACK
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ItemListCarouselBinding
import com.rexrama.aficionado.utils.Util

class CarouselAdapter :
    PagingDataAdapter<ListStoryItem, CarouselAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallBack: Util.OnItemClickCallBack? = null

    class ListViewHolder(var binding: ItemListCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val carouselItem = getItem(position)
        if (carouselItem != null) {
            Glide.with(holder.itemView.context)
                .load(carouselItem.photoUrl)
                .into(holder.binding.carouselImageView)
            holder.apply {
                binding.tvCarouselUsername.text = carouselItem.name
                itemView.setOnClickListener {
                    onItemClickCallBack?.onItemClicked(carouselItem)
                }
            }

        }
    }

    fun setOnItemClickCallback(onItemClickCallBack: Util.OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
}