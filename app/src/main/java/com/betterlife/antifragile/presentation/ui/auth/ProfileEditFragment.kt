package com.betterlife.antifragile.presentation.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.betterlife.antifragile.presentation.ui.auth.handler.ImageHandler
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.PermissionUtil
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken
import com.betterlife.antifragile.presentation.util.TokenManager.saveTokens
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    R.layout.fragment_profile_edit
) {
    private lateinit var profileEditViewModel: ProfileEditViewModel
    private lateinit var imageHandler: ImageHandler
    private lateinit var email: String
    private lateinit var loginType: LoginType
    private var isNewMember: Boolean = false
    private var isCheckedNickname = false
    private var gender = Gender.MALE

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imageHandler.showImageSourceDialog()
            } else {
                PermissionUtil.showPermissionsDeniedDialog(this)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 프로필 수정 시 기존 정보를 불러와서 화면에 표시
        setVariables()
        setupViewModel()
        setupHandler()
        setupObservers()
        setupButtons()
        setupTextWatchers()
    }

    private fun setVariables() {
        email = ProfileEditFragmentArgs.fromBundle(requireArguments()).email
        loginType = ProfileEditFragmentArgs.fromBundle(requireArguments()).loginType
        isNewMember = ProfileEditFragmentArgs.fromBundle(requireArguments()).isNewMember

        if (isNewMember) {
            binding.btnSave.text = "회원가입"
        } else {
            binding.btnSave.text = "수정완료"
        }
    }

    private fun setupViewModel() {
        // TODO: 모든 fragment에서 accestoken이 만료된 경우 재발급 로직 추가
        val factory = ProfileEditViewModelFactory(getAccessToken(requireContext()))
        profileEditViewModel = factory.create(ProfileEditViewModel::class.java)
    }

    private fun setupHandler() {
        imageHandler = ImageHandler(this) { uri ->
            binding.ivProfileImg.setImageURI(uri)
        }
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
                saveTokens(requireContext(), it.tokenIssue.accessToken, it.tokenIssue.refreshToken)
                binding.tvSuccessMessage.visibility = View.VISIBLE
                binding.tvSuccessMessage.text = "회원가입이 완료되었습니다"
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateMainActivity()
                }, 500)
            },
            onError = {
                Log.d("ProfileEditFragment", "authSignUpResponse error: $it")
            }
        )

        setupBaseObserver(
            liveData = profileEditViewModel.profileModifyResponse,
            onSuccess = {
                binding.tvSuccessMessage.visibility = View.VISIBLE
                binding.tvSuccessMessage.text = "프로필 수정이 완료되었습니다"
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().popBackStack()
                }, 500)
            },
            onError = {
                Log.d("ProfileEditFragment", "profileModifyResponse error: $it")
            }
        )
    }

    private fun navigateMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
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
                if (!DateUtil.isValidBirthday(birthday)) {
                    showCustomToast("생년월일을 정확히 입력해주세요(ex. 2000.01.01)")
                    return@setOnClickListener
                }

                val imageMultipart = imageHandler.getSelectedImageUri()?.let { uri ->
                    val stream = requireContext().contentResolver.openInputStream(uri)
                    val requestFile =
                        stream?.readBytes()?.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    requestFile?.let {
                        MultipartBody.Part.createFormData(
                            "profileImgFile", "profile.jpg", it
                        )
                    }
                }

                if (isNewMember) {
                    profileEditViewModel.signUp(imageMultipart, convertToSignUpRequest())
                } else {
                    profileEditViewModel.modifyProfile(
                        imageMultipart, convertToProfileModifyRequest()
                    )
                }
            }

            btnProfileImg.setOnClickListener {
                if (PermissionUtil.isCameraPermissionGranted(this@ProfileEditFragment)) {
                    imageHandler.showImageSourceDialog()
                } else if (
                    PermissionUtil.shouldShowCameraRationale(this@ProfileEditFragment)
                    ) {
                    PermissionUtil.showPermissionRationaleDialog(this@ProfileEditFragment) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
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