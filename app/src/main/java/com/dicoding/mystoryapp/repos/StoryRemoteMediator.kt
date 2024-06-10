package com.dicoding.mystoryapp.repos

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.db.RemoteKeys
import com.dicoding.mystoryapp.db.PostStoryDb

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: PostStoryDb, private val apiService: ApiService, token: String
) : RemoteMediator<Int, DetailPostStory>() {
    var token: String? = token

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val LOCATION = 0

    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, DetailPostStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val detail: List<DetailPostStory>
            val respDetail = apiService.getPagingPostStory(
                page, state.config.pageSize, LOCATION, "Bearer $token"
            )

            detail = respDetail.listStory

            val endOfPaginationReached = detail.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().deleteRemoteKeys()
                    with(database) { getDetailPostStoryDao().deleteAllStories() }
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = detail.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.getRemoteKeysDao().insertAll(keys)
                database.getDetailPostStoryDao().insertStories(detail)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(pagingState: PagingState<Int, DetailPostStory>): RemoteKeys? {
        return pagingState.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { data ->
                database.getRemoteKeysDao().getRemoteKeysId(data.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(pagingState: PagingState<Int, DetailPostStory>): RemoteKeys? {
        return pagingState.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                database.getRemoteKeysDao().getRemoteKeysId(data.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(pagingState: PagingState<Int, DetailPostStory>): RemoteKeys? {
        return pagingState.anchorPosition?.let { position ->
            pagingState.closestItemToPosition(position)?.id?.let { id ->
                database.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}