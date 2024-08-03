package com.betterlife.antifragile.presentation.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R

class EmoticonAdapter(
    private val emoticons: List<Int>,
    private val onEmoticonSelected: (Int) -> Unit
) : RecyclerView.Adapter<EmoticonAdapter.EmoticonViewHolder>() {

    inner class EmoticonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivEmoticon)

        init {
            itemView.setOnClickListener {
                onEmoticonSelected(adapterPosition)
            }
        }

        fun bind(resource: Int) {
            imageView.setImageResource(resource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoticonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emoticon_item, parent, false)
        return EmoticonViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmoticonViewHolder, position: Int) {
        holder.bind(emoticons[position])
    }

    override fun getItemCount() = emoticons.size
}