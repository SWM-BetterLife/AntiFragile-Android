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
    private var selectedDate: String? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setDates(newDates: List<CalendarDateModel>) {
        dates = newDates
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendarDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding, onDateClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dates[position], dates[position].date == selectedDate)
    }

    override fun getItemCount() = dates.size

    inner class CalendarViewHolder(
        private val binding: CalendarDayItemBinding,
        private val onDateClick: (CalendarDateModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(calendarDateModel: CalendarDateModel, isSelected: Boolean) {
            val day = calendarDateModel.date.split("-").last().toInt().toString()
            binding.tvDay.text = day
            binding.root.alpha = if (calendarDateModel.isCurrentMonth) 1f else 0.3f

            if (isSelected) {
                binding.tvDay.setTextColor(binding.root.context.getColor(R.color.main_color))
            } else {
                binding.tvDay.setTextColor(binding.root.context.getColor(R.color.black))
            }

            if (calendarDateModel.isCurrentMonth) {
                if (calendarDateModel.emoticonUrl == null) {
                    binding.ivEmoticon.setImageResource(R.drawable.emoticon_blank)
                } else {
                    Glide.with(binding.root.context)
                        .load(calendarDateModel.emoticonUrl)
                        .into(binding.ivEmoticon)
                }
                binding.ivEmoticon.visibility = View.VISIBLE
            } else {
                binding.ivEmoticon.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                selectedDate = calendarDateModel.date
                notifyDataSetChanged()
                onDateClick(calendarDateModel)
            }
        }
    }
}
