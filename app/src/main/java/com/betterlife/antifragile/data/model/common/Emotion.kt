package com.betterlife.antifragile.data.model.common

import com.betterlife.antifragile.R

enum class Emotion(val toKorean: String, val bgRes: Int) {
    JOY("기쁨", R.drawable.bg_emoticon_joy),
    PASSION("열정", R.drawable.bg_emoticon_passion),
    FLUTTER("설렘", R.drawable.bg_emoticon_flutter),
    NORMAL("평범함", R.drawable.bg_emoticon_normal),
    AMAZEMENT("놀람", R.drawable.bg_emoticon_amazement),
    ANXIETY("불안", R.drawable.bg_emoticon_anxiety),
    PANIC("당황", R.drawable.bg_emoticon_panic),
    SAD("슬픔", R.drawable.bg_emoticon_sad),
    PAIN("아픔", R.drawable.bg_emoticon_pain),
    DEPRESSION("우울", R.drawable.bg_emoticon_depression),
    JEALOUSY("질투", R.drawable.bg_emoticon_jealousy),
    ENNUI("따분", R.drawable.bg_emoticon_ennui),
    FEAR("공포", R.drawable.bg_emoticon_fear),
    ANGER("분노", R.drawable.bg_emoticon_anger),
    FATIGUE("피곤", R.drawable.bg_emoticon_fatigue),
    ERROR("오류", R.color.white),
    NOT_SELECTED("미선택", R.color.white);

    fun getBackgroundResource(): Int {
        return bgRes
    }

    companion object {
        fun fromString(value: String?): Emotion {
            return if (value == null) {
                ERROR
            } else {
                entries.find {
                    it.name.equals(value, ignoreCase = true) && it != ERROR && it != NOT_SELECTED
                } ?: ERROR
            }
        }
    }
}
