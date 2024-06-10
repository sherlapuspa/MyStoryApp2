package com.dicoding.mystoryapp.di

import android.content.Context
import com.dicoding.mystoryapp.repos.MainRepository
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.db.PostStoryDb

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val db = PostStoryDb.getDatabase(context)
        val api = ApiConfig.getApiService()
        return MainRepository(db, api)
    }
}