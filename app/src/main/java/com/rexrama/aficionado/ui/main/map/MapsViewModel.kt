package com.rexrama.aficionado.ui.main.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.data.remote.response.StoryResponse
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference): ViewModel() {
    private var apiService = ApiConfig.getApiService()

    private val _location = MutableLiveData<List<ListStoryItem>>()
    val location: MutableLiveData<List<ListStoryItem>> = _location




    fun getAllStories(token: String) {
        val client = apiService.getAllStoriesWithLocation(token, 1)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _location.value = responseBody.listStory
                    }
                } else {
                    onFailureLog(response.message())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                onFailureLog(t.message)
            }
        })
    }

    private fun onFailureLog(message: String?){
        Log.e(TAG, "onFailure: $message")
    }


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }


    companion object {
        private const val TAG = "MapViewModel"
    }
}