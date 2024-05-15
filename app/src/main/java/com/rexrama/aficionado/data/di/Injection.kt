package com.rexrama.aficionado.data.di

import com.rexrama.aficionado.data.local.repo.StoryRepo
import com.rexrama.aficionado.data.remote.api.ApiConfig

object Injection {
    fun provideRepos(): StoryRepo {
        val apiService = ApiConfig.getApiService()
        return StoryRepo(apiService)
    }
}