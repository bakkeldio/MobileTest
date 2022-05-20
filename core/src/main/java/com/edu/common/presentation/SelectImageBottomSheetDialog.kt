package com.edu.common.presentation

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edu.common.R
import com.edu.common.databinding.FragmentSelectLogoBottomsheetBinding
import com.edu.common.presentation.model.CropImageShapeEnum
import com.edu.common.utils.getOriginalFileName
import com.edu.common.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImageView

class SelectImageBottomSheetDialog(
    private val uri: Uri,
    private val listener: Listener,
    private val cropImageShape: CropImageShapeEnum = CropImageShapeEnum.OVAL
) :
    BottomSheetDialogFragment() {


    companion object {
        const val TAG = "SELECT_PROFILE_LOGO"
    }

    private val binding by viewBinding(FragmentSelectLogoBottomsheetBinding::bind)

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
            .inflate(R.layout.fragment_select_logo_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cropImageView.cropShape = when (cropImageShape) {
            CropImageShapeEnum.OVAL -> CropImageView.CropShape.OVAL
            CropImageShapeEnum.RECTANGLE -> CropImageView.CropShape.RECTANGLE
        }
        binding.cropImageView.setImageUriAsync(uri)

        binding.selectCroppedImageBtn.setOnClickListener {
            val fileName = uri.getOriginalFileName(requireContext()) ?: return@setOnClickListener
            listener.getCroppedImage(
                fileName,
                binding.cropImageView.croppedImage
            )
        }
    }

    interface Listener {
        fun getCroppedImage(fileName: String, bitmap: Bitmap)
    }

}