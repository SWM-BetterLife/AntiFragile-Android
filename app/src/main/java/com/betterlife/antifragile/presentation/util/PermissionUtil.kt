package com.betterlife.antifragile.presentation.util

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {

    fun isCameraPermissionGranted(fragment: Fragment): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowCameraRationale(fragment: Fragment): Boolean {
        return fragment.shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
    }

    fun showPermissionRationaleDialog(fragment: Fragment, onProceed: () -> Unit) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("권한 필요")
            .setMessage("이 기능을 사용하려면 카메라 및 갤러리 권한이 필요합니다. 계속하시겠습니까?")
            .setPositiveButton("권한 요청") { dialog, _ ->
                dialog.dismiss()
                onProceed()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showPermissionsDeniedDialog(fragment: Fragment) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("권한이 거부되었습니다")
            .setMessage("앱의 원활한 사용을 위해 카메라 및 갤러리 권한이 필요합니다. 앱 설정에서 권한을 허용해 주세요.")
            .setPositiveButton("설정으로 이동") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    "package",
                    fragment.requireContext().packageName,
                    null)
                intent.data = uri
                fragment.startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}


