package com.betterlife.antifragile.presentation.ui.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.databinding.CalendarDayItemBinding
import com.bumptech.glide.Glide

class DiaryCalendarAdapter(private val onDateClick: (CalendarDateModel) -> Unit) :
    RecyclerView.Adapter<DiaryCalendarAdapter.CalendarViewHolder>() {
    private var dates: List<CalendarDateModel> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setDates(newDates: List<CalendarDateModel>) {
        dates = newDates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendarDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, onDateClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount() = dates.size

    class CalendarViewHolder(
        private val binding: CalendarDayItemBinding,
        private val onDateClick: (CalendarDateModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(calendarDateModel: CalendarDateModel) {
            val day = calendarDateModel.date.split("-").last().toInt().toString()
            binding.tvDay.text = day
            binding.root.alpha = if (calendarDateModel.isCurrentMonth) 1f else 0.3f

            // 감정 아이콘 표시
            if (calendarDateModel.isCurrentMonth) {
                Glide.with(binding.root.context)
                    .load(calendarDateModel.emotionIconUrl ?: R.drawable.emoticon_blank)
                    .into(binding.ivEmoticon)
                binding.ivEmoticon.visibility = View.VISIBLE
            } else {
                binding.ivEmoticon.visibility = View.GONE
            }

            // 날짜 클릭 리스너 설정
            binding.root.setOnClickListener { onDateClick(calendarDateModel) }
        }
    }
}