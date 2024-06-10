package com.dicoding.mystoryapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DetailPostStory::class, RemoteKeys::class], version = 2, exportSchema = false
)
abstract class PostStoryDb : RoomDatabase() {

    abstract fun getDetailPostStoryDao(): DaoDetailPostStory
    abstract fun getRemoteKeysDao(): DaoRemoteKeys

    companion object {
        @Volatile
        private var INSTANCE: PostStoryDb? = null

        @JvmStatic
        fun getDatabase(context: Context): PostStoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, PostStoryDb::class.java, "story_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}