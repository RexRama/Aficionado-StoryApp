package com.rexrama.aficionado.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rexrama.aficionado.data.local.repo.StoryRepo
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch


class MainViewModel(private val pref: UserPreference?, private val storyRepo: StoryRepo) :
    ViewModel() {


    fun fetchStories(): LiveData<PagingData<ListStoryItem>> =
        storyRepo.getStories().cachedIn(viewModelScope)


    fun setToken(token: String) {
        storyRepo.setToken(token)
    }

    fun getUser(): LiveData<UserModel>? {
        return pref?.getUser()?.asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref?.logout()
        }
    }
}