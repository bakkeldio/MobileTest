package com.edu.common.utils.imageLoading

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.edu.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlideImageLoaderImpl @Inject constructor(@ApplicationContext val context: Context) :
    IImageLoader {

    companion object {
        const val DEFAULT_TIME_OUT = 10000
    }

    override fun loadImageWithCircleShape(
        url: String?,
        imageView: ImageView,
        error: Int?
    ) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().timeout(DEFAULT_TIME_OUT))
            .load(url)
            .circleCrop()
            .error(error ?: R.drawable.ic_chat_item)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }


    override fun loadImageCircleShapeAndSignature(
        url: String?,
        imageView: ImageView,
        millis: Long,
        error: Int?
    ) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().timeout(DEFAULT_TIME_OUT))
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .signature(ObjectKey(millis))
            .error(error ?: R.drawable.ic_chat_item)
            .into(imageView)

    }

    override fun loadImage(url: String?, imageView: ImageView) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().timeout(DEFAULT_TIME_OUT))
            .load(url)
            .placeholder(R.drawable.progress_animation)
            .into(imageView)
    }

    override fun loadImageFromUri(uri: Uri, imageView: ImageView) {
        Glide.with(context).load(uri).into(imageView)
    }

    override fun loadProfileImageCircleShapeAndSignature(
        url: String?,
        millis: Long,
        listener: (Drawable) -> Unit
    ) {
        Glide.with(context)
            .load(url)
            .circleCrop()
            .error(R.drawable.ic_chat_item)
            .placeholder(R.drawable.progress_animation)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    listener.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            }
            )
    }
}