package com.edu.common.utils.imageLoading

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.google.firebase.storage.StorageReference

interface IImageLoader {

    fun loadImageWithCircleShape(
        url: String?,
        imageView: ImageView,
        @DrawableRes error: Int? = null
    )

    fun loadImageCircleShapeAndSignature(
        url: String?,
        imageView: ImageView,
        millis: Long
    )
}