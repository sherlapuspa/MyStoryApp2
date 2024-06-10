package com.dicoding.mystoryapp.data

import android.os.Parcelable
import com.dicoding.mystoryapp.db.DetailPostStory
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RegisterUserData(
    var name: String, var email: String, var password: String
)

data class LoginUserData(
    var email: String, var password: String
)

@Parcelize
data class ResponsePostStory(

    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String
) : Parcelable

@Parcelize
data class ResponseLogin(
    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String,

    @field:SerializedName("loginResult") val loginResult: LoginResult

) : Parcelable

@Parcelize
data class LoginResult(

    @field:SerializedName("name") val name: String,

    @field:SerializedName("userId") val userId: String,

    @field:SerializedName("token") val token: String
) : Parcelable


data class ResponsePagingPostStory(

    @field:SerializedName("listStory") val listStory: List<DetailPostStory>,

    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String
)


@Parcelize
data class ResponseLocPostStory(
    @field:SerializedName("error") var error: String,

    @field:SerializedName("message") var message: String,

    @field:SerializedName("listStory") var listStory: List<DetailPostStory>
) : Parcelable





