package com.edu.mobiletestadmin.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.edu.mobiletestadmin.R


class GlideImageLoaderImpl(val context: Context) :
    IImageLoader {
    override fun loadImageWithCircleShape(
        imageView: ImageView,
        imageUrl: String?,
        errorDrawable: Int
    ) {
        Glide.with(context)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(errorDrawable)
            .into(imageView)
    }

    override fun loadImageWithUriAndCircleShape(
        uri: Uri?,
        imageView: ImageView,
        errorDrawable: Int
    ) {
        Glide.with(context)
            .load(uri)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(errorDrawable)
            .into(imageView)
    }

    override fun loadImageWithCircleShapeAndSignature(
        imageView: ImageView,
        imageUrl: String?,
        millis: Long,
        errorDrawable: Int
    ) {
        Glide.with(context)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(errorDrawable)
            .signature(ObjectKey(millis))
            .into(imageView)
    }

}