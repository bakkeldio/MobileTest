package com.edu.common.utils.imageLoading

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.Placeholder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.edu.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlideImageLoaderImpl @Inject constructor(@ApplicationContext val context: Context) :
    IImageLoader {
    override fun loadImageWithCircleShape(
        url: String?,
        imageView: ImageView,
        error: Int?
    ) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().timeout(10000))
            .load(url)
            .circleCrop()
            .error(error ?: R.drawable.ic_chat_item)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }


    override fun loadImageCircleShapeAndSignature(
        url: String?,
        imageView: ImageView,
        millis: Long
    ) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().timeout(10000))
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .signature(ObjectKey(millis))
            .error(R.drawable.ic_chat_item)
            .into(imageView)

    }
}