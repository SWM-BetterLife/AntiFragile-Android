package com.betterlife.antifragile.presentation.ui.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.calendar.CalendarDateModel
import com.betterlife.antifragile.databinding.ItemCalendarBinding
import com.bumptech.glide.Glide
import java.util.Calendar

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
            val isWeekend = isWeekend(calendarDateModel.date)

            binding.apply {
                tvDay.text = day
                setupVisibility(isCurrentMonth)
                setupEmoticon(calendarDateModel, isCurrentMonth, isSelected)
                setupDayAppearance(isCurrentMonth, isSelected, isWeekend)
                setupClickListener(calendarDateModel, isCurrentMonth)
            }
        }

        private fun setupVisibility(isCurrentMonth: Boolean) {
            binding.ivEmoticon.visibility = if (isCurrentMonth) View.VISIBLE else View.GONE
        }

        private fun setupEmoticon(
            calendarDateModel: CalendarDateModel,
            isCurrentMonth: Boolean,
            isSelected: Boolean
        ) {
            if (isCurrentMonth) {
                if (calendarDateModel.emoticonUrl != null) {
                    Glide.with(binding.root.context)
                        .load(calendarDateModel.emoticonUrl)
                        .into(binding.ivEmoticon)
                } else {
                    val drawableRes =
                        if (isSelected) R.drawable.ic_circle_calendar_selected
                        else R.drawable.ic_circle_calendar
                    binding.ivEmoticon.setImageDrawable(ContextCompat.getDrawable(
                        binding.root.context,
                        drawableRes
                    ))
                }
            }
        }

        private fun setupDayAppearance(
            isCurrentMonth: Boolean,
            isSelected: Boolean,
            isWeekend: Boolean
        ) {
            val context = binding.root.context
            when {
                !isCurrentMonth ->
                    binding.tvDay.setTextColor(ContextCompat.getColor(context, R.color.gray))
                isSelected -> {
                    binding.tvDay.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.tvDay.setBackgroundResource(R.drawable.circle_filled)
                }
                isWeekend -> {
                    binding.tvDay.setTextColor(ContextCompat.getColor(context, R.color.gray))
                    binding.tvDay.setBackgroundResource(0)
                }
                else -> {
                    binding.tvDay.setTextColor(ContextCompat.getColor(context, R.color.black))
                    binding.tvDay.setBackgroundResource(0)
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun setupClickListener(
            calendarDateModel: CalendarDateModel, isCurrentMonth: Boolean
        ) {
            if (isCurrentMonth) {
                binding.root.setOnClickListener {
                    selectedDate = calendarDateModel.date
                    notifyDataSetChanged()
                    onDateClick(calendarDateModel)
                }
            } else {
                binding.root.setOnClickListener(null)
            }
        }

        private fun isWeekend(date: String): Boolean {
            val calendar = Calendar.getInstance()
            val parts = date.split("-")
            calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }
    }
}
