package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.data.*


object DataDummy {
    fun generateDummyStoriesUser(): List<DetailPostStory> {
        val newsList = ArrayList<DetailPostStory>()
        for (i in 0..5) {
            val stories = DetailPostStory(
                "Title $i",
                "Ini nama",
                "Ini deskripsi",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }


    fun generateDummyLoginUser(): LoginUserData {
        return LoginUserData("jongcies@gmail.com", "qwertyui")
    }

    fun generateDummyLoginResponse(): ResponseLogin {
        val newLoginResult = LoginResult("jongcies", "jongc1es", "tokennnya")
        return ResponseLogin(false, "Login successfully", newLoginResult)
    }

    fun generateDummyRegisterUser(): RegisterUserData {
        return RegisterUserData("jongcies", "jongcies@gmail.com@gmail.com", "qwertyui")
    }
}