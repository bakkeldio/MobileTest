package com.edu.common.utils.imageLoading

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes

interface IImageLoader {

    fun loadImageWithCircleShape(
        url: String?,
        imageView: ImageView,
        @DrawableRes error: Int? = null
    )

    fun loadImageCircleShapeAndSignature(
        url: String?,
        imageView: ImageView,
        millis: Long,
        @DrawableRes error: Int? = null
    )

    fun loadImage(
        url: String?,
        imageView: ImageView
    )

    fun loadImageFromUri(
        uri: Uri,
        imageView: ImageView
    )

    fun loadProfileImageCircleShapeAndSignature(
        url: String?,
        millis: Long,
        listener: (Drawable) -> Unit
    )
}