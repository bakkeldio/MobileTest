package com.edu.mobiletestadmin.utils

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes

interface IImageLoader {

    fun loadImageWithCircleShape(
        imageView: ImageView,
        imageUrl: String?,
        @DrawableRes errorDrawable: Int
    )

    fun loadImageWithUriAndCircleShape(
        uri: Uri?,
        imageView: ImageView,
        @DrawableRes errorDrawable: Int
    )

    fun loadImageWithCircleShapeAndSignature(
        imageView: ImageView,
        imageUrl: String?,
        millis: Long,
        @DrawableRes errorDrawable: Int
    )
}