package com.betterlife.antifragile.presentation.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.enums.Gender
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.databinding.FragmentProfileEditBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken
import com.betterlife.antifragile.presentation.util.TokenManager.saveTokens
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    R.layout.fragment_profile_edit
) {
    private lateinit var profileEditViewModel: ProfileEditViewModel
    private lateinit var email: String
    private lateinit var loginType: LoginType
    private lateinit var photoFile: File
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null
    private var isNewMember: Boolean = false
    private var isCheckedNickname = false
    private var gender = Gender.MALE

    private val multiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false

            if (cameraPermissionGranted) {
                showImageSourceDialog()
            } else {
                showPermissionsDeniedDialog()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri?.let { uri ->
                    binding.ivProfileImg.setImageURI(uri)
                    selectedImageFile = photoFile
                }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.ivProfileImg.setImageURI(uri)
                selectedImageFile = getRealPathFromURI(uri)?.let { path -> File(path) }

                if (selectedImageFile == null) {
                    Log.e("ProfileEditFragment", "Failed to get file from URI: $uri")
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 프로필 수정 시 기존 정보를 불러와서 화면에 표시
        setVariables()
        setupViewModel()
        setupObservers()
        setupButtons()
        setupTextWatchers()
    }

    private fun setVariables() {
        email = ProfileEditFragmentArgs.fromBundle(requireArguments()).email
        loginType = ProfileEditFragmentArgs.fromBundle(requireArguments()).loginType
        isNewMember = ProfileEditFragmentArgs.fromBundle(requireArguments()).isNewMember
    }

    private fun setupViewModel() {
        // TODO: 모든 fragment에서 accestoken이 만료된 경우 재발급 로직 추가
        val factory = ProfileEditViewModelFactory(getAccessToken(requireContext()))
        profileEditViewModel = factory.create(ProfileEditViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        setupBaseObserver(
            liveData = profileEditViewModel.checkNicknameResponse,
            onSuccess = {
                binding.apply {
                    if (it.isDuplicated) {
                        tvNicknameDuplicateResult.visibility = View.VISIBLE
                        tvNicknameDuplicateResult.text = "이미 사용중인 닉네임입니다"
                        tvNicknameDuplicateResult.setTextColor(resources.getColor(R.color.red))
                    } else {
                        tvNicknameDuplicateResult.visibility = View.VISIBLE
                        tvNicknameDuplicateResult.text = "사용 가능한 닉네임입니다"
                        tvNicknameDuplicateResult.setTextColor(resources.getColor(R.color.green))
                    }
                }
                isCheckedNickname = true
            },
            onError = {
                Log.d("ProfileEditFragment", "checkNicknameResponse error: $it")
            }
        )

        setupBaseObserver(
            liveData = profileEditViewModel.authSignUpResponse,
            onSuccess = {
                // TODO: 회원가입 성공 뷰 띄우기
                saveTokens(requireContext(), it.tokenIssue.accessToken, it.tokenIssue.refreshToken)
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            },
            onError = {
                Log.d("ProfileEditFragment", "authSignUpResponse error: $it")
            }
        )

        setupBaseObserver(
            liveData = profileEditViewModel.profileModifyResponse,
            onSuccess = {
                // TODO: 프로필 수정 성공 뷰 띄우기
                findNavController().popBackStack()
            },
            onError = {
                Log.d("ProfileEditFragment", "profileModifyResponse error: $it")
            }
        )
    }

    private fun setupTextWatchers() {
        binding.apply {
            etNickname.addTextChangedListener {
                // 닉네임 입력 시 중복 체크를 다시 하도록 설정
                isCheckedNickname = false
                tvNicknameDuplicateResult.visibility = View.INVISIBLE
                validateInputs()
            }
            etBirthday.addTextChangedListener { validateInputs() }
            etJob.addTextChangedListener { validateInputs() }
        }
    }

    // 중복 체크를 했고, 입력 칸이 빈 공백이 아닐 때만 저장 버튼 활성화
    private fun validateInputs() {
        binding.apply {
            val isNicknameValid = etNickname.text.toString().isNotBlank()
            val isBirthdayValid = etBirthday.text.toString().isNotBlank()
            val isJobValid = etJob.text.toString().isNotBlank()

            val isFormValid = isNicknameValid && isBirthdayValid && isJobValid
            val color = if (isFormValid) {
                ContextCompat.getColor(requireContext(), R.color.main_color)
            } else {
                ContextCompat.getColor(requireContext(), R.color.light_gray_2)
            }

            btnSave.apply {
                isEnabled = isFormValid
                backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            btnSave.setOnClickListener {
                if (!isCheckedNickname) {
                    showCustomToast("닉네임 중복 확인을 해주세요")
                    return@setOnClickListener
                }
                val birthday = etBirthday.text.toString()
                if (!isValidBirthdayFormat(birthday)) {
                    showCustomToast("생년월일을 정확히 입력해주세요(ex. 2000.01.01)")
                    return@setOnClickListener
                }

                val imageMultipart: MultipartBody.Part? = selectedImageFile?.let { file ->
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("profileImgFile", file.name, requestFile)
                }

                if (isNewMember) {
                    profileEditViewModel.signUp(
                        imageMultipart,
                        convertToSignUpRequest()
                    )
                } else {
                    profileEditViewModel.modifyProfile(
                        imageMultipart,
                        convertToProfileModifyRequest()
                    )
                }
            }

            btnProfileImg.setOnClickListener {
                checkPermissionsAndProceed()
            }

            btnDuplicateCheck.setOnClickListener {
                if (etNickname.text.toString().isBlank()) {
                    showCustomToast("닉네임을 입력해주세요")
                    return@setOnClickListener
                }
                profileEditViewModel.checkNicknameInvalid(etNickname.text.toString())
            }

            btnMan.setOnClickListener {
                if (gender == Gender.FEMALE) {
                    btnMan.setBackgroundResource(R.drawable.btn_gender_left_active)
                    btnMan.setTextColor(resources.getColor(R.color.white))
                    btnWoman.setBackgroundResource(R.drawable.btn_gender_right_inactive)
                    btnWoman.setTextColor(resources.getColor(R.color.main_color))
                    gender = Gender.MALE
                }
            }

            btnWoman.setOnClickListener {
                if (gender == Gender.MALE) {
                    btnMan.setBackgroundResource(R.drawable.btn_gender_left_inactive)
                    btnMan.setTextColor(resources.getColor(R.color.main_color))
                    btnWoman.setBackgroundResource(R.drawable.btn_gender_right_active)
                    btnWoman.setTextColor(resources.getColor(R.color.white))
                    gender = Gender.FEMALE
                }
            }
        }
    }

    private fun isValidBirthdayFormat(birthday: String): Boolean {
        val regex = Regex("""\d{4}\.\d{2}\.\d{2}""")
        return regex.matches(birthday)
    }

    private fun convertToSignUpRequest(): AuthSignUpRequest {
        val birthday = binding.etBirthday.text.toString()
        val age = calculateKoreanAge(birthday)
        return AuthSignUpRequest(
            email = email,
            loginType = loginType,
            nickname = binding.etNickname.text.toString(),
            age = age,
            gender = gender,
            job = binding.etJob.text.toString()
        )
    }

    private fun convertToProfileModifyRequest(): MemberProfileModifyRequest {
        val birthday = binding.etBirthday.text.toString()
        val age = calculateKoreanAge(birthday)
        return MemberProfileModifyRequest(
            nickname = binding.etNickname.text.toString(),
            age = age,
            gender = gender,
            job = binding.etJob.text.toString()
        )
    }

    private fun calculateKoreanAge(birthday: String): Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val birthYear = birthday.substring(0, 4).toInt()

        return currentYear - birthYear + 1
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        // 시도 1: MediaStore.Images.Media.DATA로 파일 경로 얻기
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val path = cursor.getString(columnIndex)
                if (path != null) return path
            }
        }

        // 시도 2: Content URI 직접 처리 (파일 경로 없이 파일을 바로 사용)
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = createTempFile("image", ".jpg", requireContext().cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkPermissionsAndProceed() {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            showImageSourceDialog()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showPermissionRationaleDialog()
            } else {
                multiplePermissionLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA)
                )
            }
        }
    }


    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 필요")
            .setMessage("이 기능을 사용하려면 카메라 및 갤러리 권한이 필요합니다. 계속하시겠습니까?")
            .setPositiveButton("권한 요청") { dialog, _ ->
                dialog.dismiss()
                multiplePermissionLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA)
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showPermissionsDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한이 거부되었습니다")
            .setMessage("앱의 원활한 사용을 위해 카메라 및 갤러리 권한이 필요합니다. 앱 설정에서 권한을 허용해 주세요.")
            .setPositiveButton("설정으로 이동") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        AlertDialog.Builder(requireContext())
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        photoFile = createImageFile()
        selectedImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
        cameraLauncher.launch(selectedImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            if (isNewMember) {
                setSubTitle("회원가입")
            } else {
                setSubTitle("프로필 수정")
            }
            showBackButton {
                requireView().findNavController().popBackStack()
            }
        }
    }
}