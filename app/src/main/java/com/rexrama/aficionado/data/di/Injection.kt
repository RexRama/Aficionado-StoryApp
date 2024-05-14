package com.rexrama.aficionado.data.di

import android.content.Context
import com.rexrama.aficionado.data.local.repo.StoryRepo
import com.rexrama.aficionado.data.remote.api.ApiConfig

object Injection {
    fun provideRepos(context: Context): StoryRepo {
        val apiService = ApiConfig.getApiService()
        return StoryRepo(apiService)
    }
}