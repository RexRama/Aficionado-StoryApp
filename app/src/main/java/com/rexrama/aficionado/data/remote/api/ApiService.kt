package com.rexrama.aficionado.data.remote.api

import com.rexrama.aficionado.data.model.RegisterModel
import com.rexrama.aficionado.data.model.SignInModel
import com.rexrama.aficionado.data.remote.response.LoginResponse
import com.rexrama.aficionado.data.remote.response.RegisterResponse
import com.rexrama.aficionado.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    //User Register
    @POST("register")
    fun userRegister(
        @Body user: RegisterModel
    ): Call<RegisterResponse>

    //User Login
    @POST("login")
    fun userSignIn(
        @Body user: SignInModel
    ): Call<LoginResponse>

    //Get Stories
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    //Upload Story with location
    @Multipart
    @POST("stories")
    fun postStoryWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<StoryResponse>

    @GET("stories")
    fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<StoryResponse>

    //UploadStory
    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<StoryResponse>

}