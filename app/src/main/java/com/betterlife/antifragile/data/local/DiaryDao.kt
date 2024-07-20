package com.betterlife.antifragile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.betterlife.antifragile.data.model.diary.DiaryEntry
import java.time.LocalDate

@Dao
interface DiaryDao {
    @Insert
    suspend fun insert(diaryEntry: DiaryEntry)

    // TODO: 수정 관련 메서드 추가 해서 하기

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getDiaryById(id: String): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE date = :date")
    suspend fun getDiaryByDate(date: LocalDate): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getDiariesByMonth(startDate: LocalDate, endDate: LocalDate): List<DiaryEntry>
}