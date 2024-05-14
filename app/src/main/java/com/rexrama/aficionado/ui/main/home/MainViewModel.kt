package com.rexrama.aficionado.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rexrama.aficionado.data.local.repo.StoryRepo
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.data.remote.response.StoryResponse
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel(private val pref: UserPreference?, private val storyRepo: StoryRepo) :
    ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories


    fun fetchStories(token: String) {
        _loading.value = true
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _loading.value = false
                    _stories.value = response.body()?.listStory ?: emptyList()
                } else {
                    _loading.value = false
                    Log.e("StoryViewModel", "Failed to fetch stories: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _loading.value = false
                Log.e("StoryViewModel", "Failed to fetch stories: ${t.message}")
            }

        })
    }

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