package com.dicoding.mystoryapp.repos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.data.ResponseLogin
import com.dicoding.mystoryapp.DataDummy.generateDummyStoriesUser
import com.dicoding.mystoryapp.DataDummy.generateDummyLoginUser
import com.dicoding.mystoryapp.DataDummy.generateDummyRegisterUser
import com.dicoding.mystoryapp.DataDummy.generateDummyLoginResponse
import com.dicoding.mystoryapp.MainDispatcherRule
import com.dicoding.mystoryapp.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File


class MainRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainRepository: MainRepository

    @Mock
    private var mockFile = File("nameFile")

    @Before
    fun setUp() {
        mainRepository = Mockito.mock(MainRepository::class.java)
    }

    @Test
    fun `verify getResponseLogin function is working`() {
        val dummyLoginUser = generateDummyLoginUser()
        val dummyLoginResponse = generateDummyLoginResponse()

        val expectedLoginResponse = MutableLiveData<ResponseLogin>()
        expectedLoginResponse.value = dummyLoginResponse

        mainRepository.getResponseLogin(dummyLoginUser)

        Mockito.verify(mainRepository).getResponseLogin(dummyLoginUser)
        `when`(mainRepository.authenticatedUser).thenReturn(expectedLoginResponse)

        val actualLoginResponse = mainRepository.authenticatedUser.getOrAwaitValue()
        Mockito.verify(mainRepository).authenticatedUser
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLoginResponse.value, actualLoginResponse)
    }

    @Test
    fun `when login should return the right response and not null`() {
        val dummyLoginResponse = generateDummyLoginResponse()

        val expectedLoginResponse = MutableLiveData<ResponseLogin>()
        expectedLoginResponse.value = dummyLoginResponse

        `when`(mainRepository.authenticatedUser).thenReturn(expectedLoginResponse)
        val actualLoginResponse = mainRepository.authenticatedUser.getOrAwaitValue()

        Mockito.verify(mainRepository).authenticatedUser
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLoginResponse.value, actualLoginResponse)
    }

    @Test
    fun `verify getResponseRegister function is working`() {
        val dummyRegisterUser = generateDummyRegisterUser()
        val expectedRegisterResponse = MutableLiveData<String>()
        expectedRegisterResponse.value = "Registration Account Successfully Created"

        mainRepository.getResponseRegister(dummyRegisterUser)

        Mockito.verify(mainRepository).getResponseRegister(dummyRegisterUser)
        `when`(mainRepository.registerStatus).thenReturn(expectedRegisterResponse)

        val actualRegisterResponse = mainRepository.registerStatus.getOrAwaitValue()

        Mockito.verify(mainRepository).registerStatus
        assertNotNull(actualRegisterResponse)
        assertEquals(expectedRegisterResponse.value, actualRegisterResponse)
    }

    @Test
    fun `verify upload function is working`() {
        val expectedUploadResponse = MutableLiveData<String>()
        expectedUploadResponse.value = "Story Uploaded Successfully"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", "nameFile", requestImageFile
        )
        val description: RequestBody = "deskripsinya".toRequestBody("text/plain".toMediaType())
        val token = "tokennya"
        val latlng = LatLng(1.1, 1.1)

        mainRepository.upload(
            imageMultipart, description, latlng.latitude, latlng.longitude, token
        )

        Mockito.verify(mainRepository).upload(
            imageMultipart, description, latlng.latitude, latlng.longitude, token
        )

        `when`(mainRepository.uploadStatus).thenReturn(expectedUploadResponse)

        val actualUploadResponse = mainRepository.uploadStatus.getOrAwaitValue()

        Mockito.verify(mainRepository).uploadStatus
        assertNotNull(actualUploadResponse)
        assertEquals(expectedUploadResponse.value, actualUploadResponse)
    }

    @Test
    fun `verify getStories function is working`() {
        val dummyStoriesUser = generateDummyStoriesUser()
        val expectedStoriesUser = MutableLiveData<List<DetailPostStory>>()
        expectedStoriesUser.value = dummyStoriesUser

        val token = "tokennya"
        mainRepository.getUserStories(token)
        Mockito.verify(mainRepository).getUserStories(token)

        `when`(mainRepository.userStories).thenReturn(expectedStoriesUser)

        val actualStoriesUser = mainRepository.userStories.getOrAwaitValue()

        Mockito.verify(mainRepository).userStories

        assertNotNull(actualStoriesUser)
        assertEquals(expectedStoriesUser.value, actualStoriesUser)
        assertEquals(dummyStoriesUser.size, actualStoriesUser.size)
    }

    @Test
    fun `when stories should return the right data and not null`() {
        val dummyStoriesUser = generateDummyStoriesUser()
        val expectedStoriesUser = MutableLiveData<List<DetailPostStory>>()
        expectedStoriesUser.value = dummyStoriesUser

        `when`(mainRepository.userStories).thenReturn(expectedStoriesUser)

        val actualStoriesUser = mainRepository.userStories.getOrAwaitValue()

        Mockito.verify(mainRepository).userStories

        assertNotNull(actualStoriesUser)
        assertEquals(expectedStoriesUser.value, actualStoriesUser)
        assertEquals(dummyStoriesUser.size, actualStoriesUser.size)
    }

    @Test
    fun `when upload response should return the right data and not null`() {
        val expectedUploadResponse = MutableLiveData<String>()
        expectedUploadResponse.value = "Story Uploaded Successfully"

        `when`(mainRepository.uploadStatus).thenReturn(expectedUploadResponse)

        val actualUploadResponse = mainRepository.uploadStatus.getOrAwaitValue()

        Mockito.verify(mainRepository).uploadStatus
        assertNotNull(actualUploadResponse)
        assertEquals(expectedUploadResponse.value, actualUploadResponse)
    }

    @Test
    fun `when loading should return the right data and not null`() {
        val expectedLoadingResponse = MutableLiveData<Boolean>()
        expectedLoadingResponse.value = true

        `when`(mainRepository.isLoad).thenReturn(expectedLoadingResponse)

        val actualLoadingResponse = mainRepository.isLoad.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoad
        assertNotNull(actualLoadingResponse)
        assertEquals(expectedLoadingResponse.value, actualLoadingResponse)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `verify getPagingPostStory function is working and not null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStoriesUser = generateDummyStoriesUser()
        val data = PagedTestDataSources.snapshot(dummyStoriesUser)
        val story = MutableLiveData<PagingData<DetailPostStory>>()
        val token = "tokennya"
        story.value = data

        `when`(mainRepository.getPagingStories(token)).thenReturn(story)

        val actualData = mainRepository.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapt.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(mainRepository).getPagingStories(token)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStoriesUser.size, differ.snapshot().size)
        assertEquals(dummyStoriesUser[0], differ.snapshot()[0])
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<DetailPostStory>>>() {
        companion object {
            fun snapshot(items: List<DetailPostStory>): PagingData<DetailPostStory> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<DetailPostStory>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DetailPostStory>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}