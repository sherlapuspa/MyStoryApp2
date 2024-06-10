package com.dicoding.mystoryapp.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoDetailPostStory {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<DetailPostStory>)

    @Query("SELECT * FROM stories")
    fun getPagedStories(): PagingSource<Int, DetailPostStory>

    @Query("SELECT * FROM stories")
    fun getAllStoriesList(): List<DetailPostStory>

    @Query("DELETE FROM stories")
    suspend fun deleteAllStories()
}