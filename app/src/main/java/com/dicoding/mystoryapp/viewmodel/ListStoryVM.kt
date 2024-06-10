package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.data.LoginUserData
import com.dicoding.mystoryapp.data.RegisterUserData
import com.dicoding.mystoryapp.data.ResponseLogin
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.repos.MainRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ListStoryVM(private val mainRepository: MainRepository) : ViewModel() {

    val userStories: LiveData<List<DetailPostStory>>by lazy{
        mainRepository.userStories }

    val isRegistering: LiveData<String> by lazy {mainRepository.registerStatus}
    val isLoggingin: LiveData<String> by lazy { mainRepository.loginStatus}
    val isUploading: LiveData<String> by lazy {mainRepository.uploadStatus}
    val isLocating: LiveData<String> by lazy {mainRepository.locStatus}


    val isLoad: LiveData<Boolean> by lazy{ mainRepository.isLoad}
    val userlogin: LiveData<ResponseLogin> by lazy{ mainRepository.authenticatedUser}


    fun login(loginUserData: LoginUserData) {
        mainRepository.getResponseLogin(loginUserData)
    }

    fun register(registerUserData: RegisterUserData) {
        mainRepository.getResponseRegister(registerUserData)
    }

    fun upload(
        photo: MultipartBody.Part, des: RequestBody, lat: Double?, lng: Double?, token: String
    ) {
        mainRepository.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<DetailPostStory>> {
        return mainRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        mainRepository.getUserStories(token)
    }
}
