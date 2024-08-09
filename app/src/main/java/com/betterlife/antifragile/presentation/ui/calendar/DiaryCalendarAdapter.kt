package com.betterlife.antifragile.presentation.ui.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.databinding.ItemCalendarBinding
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
        val binding = ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarViewHolder(binding, onDateClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dates[position], dates[position].date == selectedDate)
    }

    override fun getItemCount() = dates.size

    inner class CalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val onDateClick: (CalendarDateModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(calendarDateModel: CalendarDateModel, isSelected: Boolean) {
            val day = calendarDateModel.date.split("-").last().toInt().toString()
            val isCurrentMonth = calendarDateModel.isCurrentMonth

            binding.apply {
                tvDay.text = day
                if (isCurrentMonth) {
                    ivEmoticon.visibility = View.VISIBLE
                    if (calendarDateModel.emoticonUrl != null) {
                        Glide.with(binding.root.context)
                            .load(calendarDateModel.emoticonUrl)
                            .into(binding.ivEmoticon)
                    } else {
                        binding.ivEmoticon.setImageDrawable(null)
                    }

                    if (isSelected) {
                        tvDay.setTextColor(binding.root.context.getColor(R.color.white))
                        tvDay.setBackgroundResource(R.drawable.circle_filled)
                        tvDay.backgroundTintList =
                            binding.root.context.getColorStateList(R.color.calender_date_selected_color)

                        if (calendarDateModel.diaryId == null) {
                            ivEmoticon.backgroundTintList =
                                binding.root.context.getColorStateList(R.color.calendar_emoticon_selected_color)
                        }
                    } else {
                        tvDay.setTextColor(binding.root.context.getColor(R.color.black))
                        tvDay.setBackgroundResource(0)

                        if (calendarDateModel.diaryId == null) {
                            ivEmoticon.backgroundTintList =
                                binding.root.context.getColorStateList(R.color.light_gray_2)
                        }
                    }

                    root.setOnClickListener {
                        selectedDate = calendarDateModel.date
                        notifyDataSetChanged()
                        onDateClick(calendarDateModel)
                    }
                } else {
                    tvDay.setTextColor(binding.root.context.getColor(R.color.gray))
                    ivEmoticon.visibility = View.GONE
                }
            }
        }
    }
}
