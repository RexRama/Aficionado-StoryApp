package com.rexrama.aficionado.data.local.repo

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rexrama.aficionado.data.local.paging.StoriesPagingSource
import com.rexrama.aficionado.data.remote.api.ApiService
import com.rexrama.aficionado.data.remote.response.ListStoryItem


class StoryRepo(private val apiService: ApiService) {

    private var token = ""

    fun setToken(token: String) {
        this.token = token
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 4
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService, token)
            }
        ).liveData
    }

}