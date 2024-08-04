package com.betterlife.antifragile.presentation.ui.diary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonByEmotion
import com.betterlife.antifragile.databinding.ItemEmoticonBinding
import com.bumptech.glide.Glide

class EmoticonByEmotionAdapter(
    private val onEmoticonSelected: (Int) -> Unit
) : RecyclerView.Adapter<EmoticonByEmotionAdapter.EmoticonViewHolder>() {

    private var emoticons: List<EmoticonByEmotion> = emptyList()


    inner class EmoticonViewHolder(
        private val binding: ItemEmoticonBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onEmoticonSelected(adapterPosition)
            }
        }

        fun bind(emoticon: EmoticonByEmotion) {
            Glide.with(binding.ivEmoticon)
                .load(emoticon.imgUrl)
                .into(binding.ivEmoticon)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoticonViewHolder {
        val binding = ItemEmoticonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmoticonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmoticonViewHolder, position: Int) {
        holder.bind(emoticons[position])
    }

    override fun getItemCount(): Int = emoticons.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEmoticons(newEmoticons: List<EmoticonByEmotion>) {
        emoticons = newEmoticons
        notifyDataSetChanged()
    }

    fun getSelectedEmoticon(position: Int): EmoticonByEmotion {
        return emoticons[position]
    }
}