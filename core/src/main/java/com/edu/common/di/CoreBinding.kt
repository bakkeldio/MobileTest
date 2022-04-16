package com.edu.common.di

import com.edu.common.utils.imageLoading.GlideImageLoaderImpl
import com.edu.common.utils.imageLoading.IImageLoader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoreBinding {

    @Binds
    fun bindImageLoader(glideImageLoaderImpl: GlideImageLoaderImpl): IImageLoader

}