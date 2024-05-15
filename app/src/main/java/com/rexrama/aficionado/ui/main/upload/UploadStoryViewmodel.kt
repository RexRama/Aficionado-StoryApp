package com.rexrama.aficionado.ui.main.upload

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.StoryResponse
import com.rexrama.aficionado.utils.DialogUtils
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewmodel(private val pref: UserPreference, private val dialogUtils: DialogUtils) :
    ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _finishActivity = MutableLiveData<Boolean>()
    val finishActivity: LiveData<Boolean> = _finishActivity

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun postStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) {
        _isLoading.value = true
        val client = if (lat == null && lon == null) apiService.postStory(
            token,
            imageMultipart,
            description
        ) else apiService.postStoryWithLocation(token, imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        dialogUtils.showResultDialog(
                            "Upload Success!",
                            "Upload Story Successful!",
                        ) {
                            _finishActivity.value = true
                        }
                    }
                } else {
                    dialogUtils.showErrorDialog("Upload Failed", response.message())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                dialogUtils.showErrorDialog("Upload Failed", t.message)
            }

        })
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _location.value = location
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}