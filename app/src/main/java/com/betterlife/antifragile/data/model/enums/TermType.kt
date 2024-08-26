package com.betterlife.antifragile.data.model.enums

import com.betterlife.antifragile.R

enum class TermType(val titleResId: Int, val content: String, var isAgreed: Boolean = false) {
    SERVICE_TERM(
        R.string.service_term,
        "https://decorous-jupiter-fb4.notion.site/Antifragile-f8684923954a4978ae72f294cf642bd2?pvs=4"
    ),
    PRIVACY_TERM(
        R.string.privacy_term,
        "https://decorous-jupiter-fb4.notion.site/ead2d592646040e1af36427b2c33371c?pvs=4"
    ),
    MARKETING_TERM(
        R.string.marketing_term,
        "https://decorous-jupiter-fb4.notion.site/4ead0fcd5a9544bb94bf3201e27da87b?pvs=4"
    ),
    DIARY_TERM(
        R.string.diary_term,
        "https://decorous-jupiter-fb4.notion.site/753694ec614c4af7a0ff2c8cba2c8531?pvs=4"
    )
}