package com.edu.mobiletest.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.edu.common.navigation.Navigation
import com.edu.common.presentation.BaseFragment
import com.edu.common.presentation.ResourceState
import com.edu.common.presentation.SelectLogoBottomSheetDialog
import com.edu.common.utils.imageLoading.IImageLoader
import com.edu.common.utils.showToast
import com.edu.mobiletest.BuildConfig
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentProfileBinding
import com.edu.mobiletest.ui.LoginActivity
import com.edu.mobiletest.ui.profile.adapter.CompletedTests
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding>(
        R.layout.fragment_profile,
        FragmentProfileBinding::bind
    ), ChangeProfilePhotoBottomSheetFragment.Listener, SelectLogoBottomSheetDialog.Listener {

    @Inject
    lateinit var imageLoader: IImageLoader

    override val viewModel: ProfileViewModel by viewModels()

    private var changeProfileLogoBottomSheetDialog: ChangeProfilePhotoBottomSheetFragment? = null

    private var selectLogoBottomSheet: SelectLogoBottomSheetDialog? = null

    private var groupId: String = ""

    private var profileAvatar: String? = null

    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getProfile()
    }


    private val selectImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { it ->
                showSelectLogoBottomSheet(it)
            }
        }

    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                fileUri?.let { uri ->
                    showSelectLogoBottomSheet(uri)
                }
            }
        }

    override fun setupUI() {
        binding.signOut.setOnClickListener {
            viewModel.signOut()
        }
        binding.profileImage.setOnClickListener {
            changeProfileLogoBottomSheetDialog =
                ChangeProfilePhotoBottomSheetFragment(this, profileAvatar == null)
            changeProfileLogoBottomSheetDialog?.show(childFragmentManager, "")

        }

    }

    override fun setupVM() {
        super.setupVM()
        viewModel.signOut.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResourceState.Success -> {
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is ResourceState.Error -> {
                    showToast(state.message)
                }
                else -> Unit
            }
        }

        viewModel.studentProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                groupId = profile.groupId
                profileAvatar = profile.avatar
                startLoadUserImage(profile.avatar, false)
                viewModel.fetchCompletedTests(profile.groupId)
                binding.profileName.text = profile.name
                binding.profileGroup.text = profile.groupName
            }
        }
        viewModel.teacherProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                profileAvatar = profile.avatar
                startLoadUserImage(profile.avatar, false)
                binding.profileName.text = profile.fullName
                binding.profileGroup.text = profile.position
            }
        }
        viewModel.completedTests.observe(viewLifecycleOwner) {
            if (it != null) {
                val adapter = CompletedTests { test ->
                    val pair = Navigation.Tests.navigateToCompletedTest(groupId, test.uid)
                    findNavController().navigate(pair.first, pair.second)
                }
                binding.noCompletedTests.isVisible = it.isEmpty()
                binding.finishedTests.adapter = adapter
                adapter.sendTests(it)
            }
        }

        viewModel.startToDownload.observe(viewLifecycleOwner) { url ->
            startLoadUserImage(url, true)
            viewModel.updateProfileImage(url)
        }
    }

    private fun startLoadUserImage(url: String?, newImage: Boolean) {
        if (newImage) {
            imageLoader.loadImageCircleShapeAndSignature(
                url,
                binding.profileImage,
                Calendar.getInstance().timeInMillis
            )
        } else {
            imageLoader.loadImageWithCircleShape(url, binding.profileImage)
        }
    }

    private fun createTempFileUri(): Uri {
        val file = File.createTempFile("temp_file", ".png", requireContext().cacheDir)

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }

    private fun showSelectLogoBottomSheet(uri: Uri) {
        selectLogoBottomSheet = SelectLogoBottomSheetDialog(uri, this)
        selectLogoBottomSheet?.show(
            childFragmentManager,
            SelectLogoBottomSheetDialog.TAG
        )
    }

    override fun progressLoader(show: Boolean) {
        binding.progressBar.root.isVisible = show
    }

    override fun choosePhotoBtn() {
        changeProfileLogoBottomSheetDialog?.dismiss()
        selectImageFromGallery.launch("image/*")
    }

    override fun deleteProfilePicture() {
        changeProfileLogoBottomSheetDialog?.dismiss()
        viewModel.deleteProfileAvatar()
    }

    override fun makePicture() {
        changeProfileLogoBottomSheetDialog?.dismiss()
        fileUri = createTempFileUri()
        cameraActivityResult.launch(fileUri)
    }

    override fun getCroppedImage(uri: Uri) {
        selectLogoBottomSheet?.dismiss()
        viewModel.uploadFile(uri.toString())
    }

}