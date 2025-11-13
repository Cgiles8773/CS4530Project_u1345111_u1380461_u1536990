package com.example.phase1.di

import android.content.Context
import androidx.room.Room
import com.example.phase1.data.file.ImageHandler
import com.example.phase1.data.local.AppDatabase
import com.example.phase1.data.local.ImageDao
import com.example.phase1.data.repository.ImageRepository
import com.example.phase1.data.repository.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.phase1.data.repository.VisionRepository



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageDao(appDatabase: AppDatabase): ImageDao {
        return appDatabase.imageDao()
    }

    @Provides
    @Singleton
    fun provideImageHandler(@ApplicationContext context: Context): ImageHandler {
        return ImageHandler(context)
    }

    @Provides
    @Singleton
    fun provideImageRepository(imageDao: ImageDao, imageHandler: ImageHandler): ImageRepository {
        return ImageRepositoryImpl(imageDao, imageHandler)
    }

    @Provides
    @Singleton
    fun provideVisionRepository(): VisionRepository {
        return VisionRepository()
    }

}