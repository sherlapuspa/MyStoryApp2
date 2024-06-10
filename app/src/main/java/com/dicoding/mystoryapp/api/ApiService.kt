package com.dicoding.mystoryapp.api

import com.dicoding.mystoryapp.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun registUser(@Body requestRegister: RegisterUserData): Call<ResponsePostStory>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginUserData): Call<ResponseLogin>

    @GET("stories")
    suspend fun getPagingPostStory(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): ResponsePagingPostStory

    @Multipart
    @POST("stories")
    fun postNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponsePostStory>

    @GET("stories")
    fun getLocPostStory(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Call<ResponseLocPostStory>
}