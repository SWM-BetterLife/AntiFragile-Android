package com.betterlife.antifragile.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {

    fun convertDateFormat(
        date: String,
        inputFormat: String = "yyyy-MM-dd",
        outputFormat: String = "yyyy.MM.dd"
    ): String {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
        val parsedDate = inputDateFormat.parse(date)
        return outputDateFormat.format(parsedDate as Date)
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun convertDateToFullFormat(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("yyyy.MM.dd.EEEE", Locale.KOREA)
        val parsedDate = inputDateFormat.parse(date)
        return outputDateFormat.format(parsedDate as Date)
    }

    fun isValidBirthday(birthday: String, format: String = "yyyy.MM.dd"): Boolean {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            val parsedDate = dateFormat.parse(birthday)
            val today = Calendar.getInstance().time
            parsedDate != null && parsedDate.before(today)
        } catch (e: Exception) {
            false
        }
    }
}