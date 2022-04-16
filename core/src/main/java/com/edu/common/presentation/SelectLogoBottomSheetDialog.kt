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
import com.edu.common.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SelectLogoBottomSheetDialog(private val uri: Uri, private val listener: Listener) :
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
        binding.cropImageView.cropShape = CropImageView.CropShape.OVAL
        binding.cropImageView.setImageUriAsync(uri)

        binding.selectCroppedImageBtn.setOnClickListener {
            val stream = ByteArrayOutputStream()
            binding.cropImageView.croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val file = File.createTempFile("cropped_image", ".png", requireContext().cacheDir)
            val bitmapDate = stream.toByteArray()
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(bitmapDate)
            fileOutputStream.flush()
            fileOutputStream.close()
            listener.getCroppedImage(Uri.fromFile(file))
        }
    }

    interface Listener {
        fun getCroppedImage(uri: Uri)
    }

}