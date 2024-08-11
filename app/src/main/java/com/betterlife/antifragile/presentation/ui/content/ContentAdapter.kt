package com.betterlife.antifragile.presentation.ui.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.data.model.content.response.Content
import com.betterlife.antifragile.databinding.ItemContentBinding
import com.bumptech.glide.Glide

class ContentAdapter(private val contentList: List<Content>) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    inner class ContentViewHolder(
        private val binding: ItemContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(content: Content) {
            binding.apply {
                //tvDate.text = content // date
                tvTitle.text = content.title
                tvLikeCount.text = content.likeNumber.toString()
                // Load thumbnail image using your preferred image loading library
                Glide.with(ivVideoThumbnail.context)
                    .load(content.thumbnailImg)
                    .into(ivVideoThumbnail)

                Glide.with(ivChannelProfile.context)
                    .load(content.channel.img)
                    .into(ivChannelProfile)
                tvChannelName.text = content.channel.name
                tvSubscribeCount.text = content.channel.subscribeNumber.toString()

                // Handle like button and other UI logic
                btnLikeContent.isSelected = content.isLiked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(contentList[position])
    }

    override fun getItemCount(): Int = contentList.size
}