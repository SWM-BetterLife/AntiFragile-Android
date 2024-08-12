package com.betterlife.antifragile.presentation.ui.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.content.response.Content
import com.betterlife.antifragile.databinding.ItemContentBinding
import com.betterlife.antifragile.presentation.util.ImageUtil.setCircleImage
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage

class ContentAdapter(
    private val contentList: List<Content>,
    private val onItemClick: (Content) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    inner class ContentViewHolder(
        private val binding: ItemContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(content: Content) {
            binding.apply {
                tvTitle.text = content.title
                tvLikeCount.text = content.likeNumber.toString()
                ivVideoThumbnail.setImage(content.thumbnailImg)

                ivChannelProfile.setCircleImage(content.channel.img)
                tvChannelName.text = content.channel.name
                tvSubscribeCount.text = content.channel.subscribeNumber.toString()

                ivLikeContent.backgroundTintList = if (content.isLiked) {
                    binding.root.context.getColorStateList(R.color.like_button_color)
                } else {
                    binding.root.context.getColorStateList(R.color.unlike_button_color)
                }

                root.setOnClickListener {
                    onItemClick(content)
                }
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