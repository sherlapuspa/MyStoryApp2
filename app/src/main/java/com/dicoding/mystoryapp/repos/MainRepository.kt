package com.dicoding.mystoryapp.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mystoryapp.data.LoginUserData
import com.dicoding.mystoryapp.data.ResponseLogin
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.data.RegisterUserData
import com.dicoding.mystoryapp.data.ResponseLocPostStory
import com.dicoding.mystoryapp.data.ResponsePostStory
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.db.PostStoryDb
import com.dicoding.mystoryapp.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(private val storyDatabase: PostStoryDb, private val apiService: ApiService) {

    private var _userStories = MutableLiveData<List<DetailPostStory>>()
    var userStories: LiveData<List<DetailPostStory>> = _userStories


    private val _msgRegistering = MutableLiveData<String>()
    val registerStatus: LiveData<String> = _msgRegistering


    private val _msgLoging = MutableLiveData<String>()
    val loginStatus: LiveData<String> = _msgLoging

    private val _msgUpload = MutableLiveData<String>()
    val uploadStatus: LiveData<String> = _msgUpload

    private val _msgLoc = MutableLiveData<String>()
    val locStatus: LiveData<String> = _msgLoc

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _authenticatedUser = MutableLiveData<ResponseLogin>()
    val authenticatedUser: LiveData<ResponseLogin> = _authenticatedUser

    fun getResponseLogin(loginUserData: LoginUserData) {
        wrapEspressoIdlingResource {
            _isLoad.value = true
            val api = ApiConfig.getApiService().loginUser(loginUserData)
            api.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>, response: Response<ResponseLogin>
                ) {
                    _isLoad.value = false
                    val respBody = response.body()

                    if (response.isSuccessful) {
                        _authenticatedUser.value = respBody!!
                        _msgLoging.value = "Welcome ${_authenticatedUser.value!!.loginResult.name}!"
                    } else {
                        when (response.code()) {
                            401 -> _msgLoging.value =
                                "Email or password you entered is incorrect, please try again."

                            408 -> _msgLoging.value =
                                "Please check your internet connection and try again."

                            else -> _msgLoging.value = "Error Message: " + response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    _isLoad.value = false
                    _msgLoging.value = "Error Message: " + t.message.toString()
                }

            })
        }
    }

    fun getResponseRegister(registerUserData: RegisterUserData) {
        wrapEspressoIdlingResource {
            _isLoad.value = true
            val api = ApiConfig.getApiService().registUser(registerUserData)
            api.enqueue(object : Callback<ResponsePostStory> {
                override fun onResponse(
                    call: Call<ResponsePostStory>, response: Response<ResponsePostStory>
                ) {
                    _isLoad.value = false
                    if (response.isSuccessful) {
                        _msgRegistering.value = "Registration successful"
                    } else {
                        when (response.code()) {
                            400 -> _msgRegistering.value = "The email is already registered"

                            408 -> _msgRegistering.value =
                                "Check your internet connection and try again"

                            else -> _msgRegistering.value = "Error: " + response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponsePostStory>, t: Throwable) {
                    _isLoad.value = false
                    _msgRegistering.value = "Error Message:  " + t.message.toString()
                }

            })
        }
    }

    fun upload(
        photo: MultipartBody.Part, desc: RequestBody, lat: Double?, lng: Double?, token: String
    ) {
        _isLoad.value = true
        val api = ApiConfig.getApiService().postNewStory(
            photo, desc, lat?.toFloat(), lng?.toFloat(), "Bearer $token"
        )
        api.enqueue(object : Callback<ResponsePostStory> {
            override fun onResponse(
                call: Call<ResponsePostStory>, response: Response<ResponsePostStory>
            ) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    val respBody = response.body()
                    if (respBody != null && !respBody.error) {
                        _msgUpload.value = respBody.message
                    }
                } else {
                    _msgUpload.value = response.message()

                }
            }

            override fun onFailure(call: Call<ResponsePostStory>, t: Throwable) {
                _isLoad.value = false
                _msgUpload.value = t.message
            }
        })
    }


    fun getUserStories(token: String) {
        _isLoad.value = true
        val api = ApiConfig.getApiService().getLocPostStory(32, 1, "Bearer $token")
        api.enqueue(object : Callback<ResponseLocPostStory> {
            override fun onResponse(
                call: Call<ResponseLocPostStory>, response: Response<ResponseLocPostStory>
            ) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    val respBody = response.body()
                    if (respBody != null) {
                        _userStories.value = respBody.listStory
                    }
                    _msgLoc.value = respBody?.message.toString()

                } else {
                    _msgLoc.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseLocPostStory>, t: Throwable) {
                _isLoad.value = false
                _msgLoc.value = t.message.toString()
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<DetailPostStory>> {
        val pager = Pager(config = PagingConfig(
            pageSize = 5
        ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.getDetailPostStoryDao().getPagedStories()
            })
        return pager.liveData
    }
}