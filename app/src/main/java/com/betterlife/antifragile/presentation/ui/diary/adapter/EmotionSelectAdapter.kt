package com.betterlife.antifragile.presentation.ui.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.emoticontheme.EmotionSelectData
import com.bumptech.glide.Glide

class EmotionSelectAdapter(
    private val emotions: List<EmotionSelectData>,
    private val onItemClick: (EmotionSelectData) -> Unit,
    private val initialEmotion: String? = null
) : RecyclerView.Adapter<EmotionSelectAdapter.EmotionViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    init {
        selectedPosition = emotions.indexOfFirst { it.emotion == initialEmotion }
    }

    inner class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emotionIcon: ImageView = itemView.findViewById(R.id.iv_emoticon)
        val emotionText: TextView = itemView.findViewById(R.id.tv_emotion)
        val container: ConstraintLayout = itemView.findViewById(R.id.lo_select_emoticon)

        init {
            itemView.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
                onItemClick(emotions[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotion, parent, false)
        return EmotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val emotion = emotions[position]
        Glide.with(holder.itemView.context).load(emotion.imgUrl).into(holder.emotionIcon)

        holder.emotionText.text = emotion.emotionEnum.toKorean

        holder.container.isSelected = (selectedPosition == position)
        if (selectedPosition == position) {
            holder.container.setBackgroundResource(R.drawable.bg_calendar_emoticon)
            holder.emotionText.setTextColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.container.setBackgroundResource(R.color.white)
            holder.emotionText.setTextColor(holder.itemView.context.getColor(R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return emotions.size
    }
}
