package com.betterlife.antifragile.presentation.util

import androidx.fragment.app.Fragment
import com.betterlife.antifragile.presentation.customview.EditTextDialog
import com.betterlife.antifragile.presentation.customview.SelectDialog

object RecommendDialogUtil {
    fun showRecommendDialogs(
        fragment: Fragment,
        onLeftButtonClicked: () -> Unit,
        onRightButtonFeedbackProvided: (String) -> Unit
    ) {
        val context = fragment.requireContext()

        val firstDialog = SelectDialog(
            context = context,
            title = "콘텐츠 재추천 받기",
            description = "금일 무료 재추천 횟수 2회 남았습니다.",
            leftButtonText = "받지 않기",
            rightButtonText = "추천 받기",
            leftButtonListener = {
                onLeftButtonClicked()
            },
            rightButtonListener = {
                val secondDialog = EditTextDialog(
                    context = context,
                    title = "어떤 영상을 추천받고 싶나요?",
                    hint = "추천받고 싶은 영상을 입력해주세요",
                    leftButtonText = "건너 뛰기",
                    rightButtonText = "입력 완료",
                    leftButtonListener = {
                        onRightButtonFeedbackProvided("")
                    },
                    rightButtonListener = { feedback ->
                        onRightButtonFeedbackProvided(feedback)
                    }
                )
                secondDialog.show()
            }
        )
        firstDialog.show()
    }
}