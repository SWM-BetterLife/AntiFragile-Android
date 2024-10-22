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
import com.betterlife.antifragile.data.model.enums.LoginType.NORMAL
import com.betterlife.antifragile.data.model.enums.MemberStatus
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.databinding.FragmentProfileEditBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.handler.ImageHandler
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.ProfileEditViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage
import com.betterlife.antifragile.presentation.util.PermissionUtil
import com.betterlife.antifragile.presentation.util.TokenManager.saveTokens
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    R.layout.fragment_profile_edit
) {
    private lateinit var profileEditViewModel: ProfileEditViewModel
    private lateinit var imageHandler: ImageHandler
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var loginType: LoginType
    private var isNewMember: Boolean = false
    private var isCheckEmail = false
    private var isCheckedNickname = false
    private var gender = Gender.MALE

    // TODO: 중복 체크 api 수정되면 제거
    private var originNickname = ""

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

        setVariables()
        setupViewModel()
        setupHandler()
        setupObservers()
        setupButtons()
        setupTextWatchers()
        setupExistedDate()
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
        val factory = ProfileEditViewModelFactory(requireContext())
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
                        // TODO: 중복 체크 api 수정되면 제거
                        if (etNickname.text.toString() == originNickname) {
                            tvNicknameDuplicateResult.visibility = View.VISIBLE
                            tvNicknameDuplicateResult.text = "사용 가능한 닉네임입니다"
                            tvNicknameDuplicateResult.setTextColor(resources.getColor(R.color.green))
                            return@apply
                        }
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
            liveData = profileEditViewModel.memberExistenceResponse,
            onSuccess = {
                binding.apply {
                    if (it.status.equals(MemberStatus.NOT_EXISTENCE)) {
                        tvEmailDuplicateResult.visibility = View.VISIBLE
                        tvEmailDuplicateResult.text = "사용 가능한 이메일입니다"
                        tvEmailDuplicateResult.setTextColor(resources.getColor(R.color.green))
                    } else {
                        tvNicknameDuplicateResult.visibility = View.VISIBLE
                        tvNicknameDuplicateResult.text = "이미 사용중인 이메일입니다"
                        tvNicknameDuplicateResult.setTextColor(resources.getColor(R.color.red))
                    }
                }
                isCheckEmail = true
            },
            onError = {
                Log.d("ProfileEditFragment", "checkNicknameResponse error: $it")
            }
        )

        setupBaseObserver(
            liveData = profileEditViewModel.memberDetailResponse,
            onSuccess = { member ->
                binding.apply {
                    etNickname.setText(member.nickname)
                    etBirthday.setText(member.birthDate)
                    etJob.setText(member.job)
                    gender = member.gender
                    updateGenderButton()
                    if (member.profileImgUrl == null) {
                        ivProfileImg.setImageResource(R.drawable.ic_member_default_profile)
                    } else {
                        ivProfileImg.setImage(member.profileImgUrl)
                    }
                    originNickname = member.nickname
                }
            },
            onError = {
                Log.d("ProfileEditFragment", "memberDetailResponse error: $it")
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
                if (!isCheckEmail && !isCheckedNickname && binding.etNickname.text.toString() != originNickname) {
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

                profileEditViewModel.signUp(imageMultipart, convertToSignUpRequest())
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

            btnEmailDuplicateCheck.setOnClickListener {
                if (etEmail.text.toString().isBlank()) {
                    showCustomToast("이메일을 입력해주세요")
                    return@setOnClickListener
                }
                profileEditViewModel.checkEmailInvalid(etEmail.text.toString(), NORMAL)
            }

            btnNicknameDuplicateCheck.setOnClickListener {
                if (etNickname.text.toString().isBlank()) {
                    showCustomToast("닉네임을 입력해주세요")
                    return@setOnClickListener
                }
                profileEditViewModel.checkNicknameInvalid(etNickname.text.toString())
            }

            btnMan.setOnClickListener {
                gender = Gender.MALE
                updateGenderButton()
            }

            btnWoman.setOnClickListener {
                gender = Gender.FEMALE
                updateGenderButton()
            }
        }
    }

    private fun updateGenderButton() {
        binding.apply {
            if (gender == Gender.FEMALE) {
                btnMan.setBackgroundResource(R.drawable.btn_gender_left_inactive)
                btnMan.setTextColor(resources.getColor(R.color.main_color))
                btnWoman.setBackgroundResource(R.drawable.btn_gender_right_active)
                btnWoman.setTextColor(resources.getColor(R.color.white))
            } else {
                btnMan.setBackgroundResource(R.drawable.btn_gender_left_active)
                btnMan.setTextColor(resources.getColor(R.color.white))
                btnWoman.setBackgroundResource(R.drawable.btn_gender_right_inactive)
                btnWoman.setTextColor(resources.getColor(R.color.main_color))
            }
        }

    }

    private fun convertToSignUpRequest(): AuthSignUpRequest {
        return AuthSignUpRequest(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString(),
            nickname = binding.etNickname.text.toString(),
            birthDate = binding.etBirthday.text.toString(),
            gender = gender,
            job = binding.etJob.text.toString(),
            loginType = NORMAL
        )
    }

    private fun convertToProfileModifyRequest(): MemberProfileModifyRequest {
        return MemberProfileModifyRequest(
            nickname = binding.etNickname.text.toString(),
            birthDate = binding.etBirthday.text.toString(),
            gender = gender,
            job = binding.etJob.text.toString()
        )
    }

    private fun setupExistedDate() {
        if (!isNewMember) {
            profileEditViewModel.getMemberDetail()
        }
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