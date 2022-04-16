package com.edu.mobiletest.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.edu.common.utils.viewBinding
import com.edu.mobiletest.R
import com.edu.mobiletest.databinding.FragmentChangeProfilePhotoBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeProfilePhotoBottomSheetFragment(
    private val listener: Listener,
    private val isAvatarEmpty: Boolean = false
) : BottomSheetDialogFragment() {


    private val binding by viewBinding(FragmentChangeProfilePhotoBottomsheetBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_change_profile_photo_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteProfilePicture.isVisible = !isAvatarEmpty
        binding.choosePhotoBtn.setOnClickListener {
            listener.choosePhotoBtn()
        }

        binding.deleteProfilePicture.setOnClickListener {
            listener.deleteProfilePicture()
        }

        binding.makePictureBtn.setOnClickListener {
            listener.makePicture()
        }

    }

    interface Listener {
        fun choosePhotoBtn()
        fun deleteProfilePicture()
        fun makePicture()
    }


}