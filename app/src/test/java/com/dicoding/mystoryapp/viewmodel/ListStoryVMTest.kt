package com.dicoding.mystoryapp.viewmodel

import org.junit.Assert.*

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.data.ResponseLogin
import com.dicoding.mystoryapp.DataDummy.generateDummyLoginUser
import com.dicoding.mystoryapp.DataDummy.generateDummyRegisterUser
import com.dicoding.mystoryapp.DataDummy.generateDummyLoginResponse
import com.dicoding.mystoryapp.DataDummy.generateDummyStoriesUser
import com.dicoding.mystoryapp.MainDispatcherRule
import com.dicoding.mystoryapp.getOrAwaitValue
import com.dicoding.mystoryapp.repos.MainRepository
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class ListStoryVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()


    @Mock
    private lateinit var mainRepository: MainRepository
    private lateinit var mainViewModel: ListStoryVM

    @Mock
    private var mockFile = File("nameFile")

    @Before
    fun setUp() {
         mainViewModel = ListStoryVM(mainRepository)

    }

//        @Test
//    fun `when upload response should return the right data and not null`() {
//        val expectedUploadResponse = "Story Uploaded Successfully"
//        val expectedLiveData = MutableLiveData<String>().apply { value = expectedUploadResponse }
//
//        `when`(mainRepository.uploadStatus).thenReturn(expectedLiveData)
//
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualUploadResponse = mainViewModel.isUploading.getOrAwaitValue()
//
//        assertNotNull(actualUploadResponse)
//        assertEquals(expectedUploadResponse, actualUploadResponse)
//    }

    @Test
    fun `when message upload should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"

        `when`(mainRepository.uploadStatus).thenReturn(expectedRegisterMessage)

        val actualRegisterMessage = mainViewModel.isUploading.getOrAwaitValue()

        Mockito.verify(mainRepository).uploadStatus
        Assert.assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

//    @Test
//    fun `when loading upload should return the right data and not null`() {
//        val expectedLoadingResponse = true
//        val expectedLiveData = MutableLiveData<Boolean>().apply { value = expectedLoadingResponse }
//
//        `when`(mainRepository.isLoad).thenReturn(expectedLiveData)
//
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()
//
//        assertNotNull(actualLoadingResponse)
//        assertEquals(expectedLoadingResponse, actualLoadingResponse)
//    }


    @Test
    fun `when loading upload should return the right data and not null`() {
        val expectedLoadingResponse = MutableLiveData<Boolean>()
        expectedLoadingResponse.value = true

        `when`(mainRepository.isLoad).thenReturn(expectedLoadingResponse)

        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoad
        assertNotNull(actualLoadingResponse)
        assertEquals(expectedLoadingResponse.value, actualLoadingResponse)
    }

//    @Test
//    fun `verify upload function is working`() {
//        val expectedUploadResponse= "Story Uploaded Successfully"
//        val expectedLiveData = MutableLiveData<String>().apply { value = expectedUploadResponse }
//
//        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "photo", "nameFile", requestImageFile
//        )
//        val description: RequestBody = "deskripsinya".toRequestBody("text/plain".toMediaType())
//        val token = "tokennya"
//        val latlng = LatLng(1.1, 1.1)
//
//        mainRepository.upload(imageMultipart, description, latlng.latitude, latlng.longitude, token)
//        Mockito.verify(mainRepository).upload(
//            imageMultipart, description, latlng.latitude, latlng.longitude, token
//        )
//
//        `when`(mainRepository.uploadStatus).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualUploadResponse = mainViewModel.isUploading.getOrAwaitValue()
//        assertNotNull(actualUploadResponse)
//        assertEquals(expectedUploadResponse, actualUploadResponse)
//    }

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

        mainRepository.upload(imageMultipart, description, latlng.latitude, latlng.longitude, token)
        Mockito.verify(mainRepository).upload(
            imageMultipart, description, latlng.latitude, latlng.longitude, token
        )

        `when`(mainRepository.uploadStatus).thenReturn(expectedUploadResponse)

        val actualUploadResponse = mainViewModel.isUploading.getOrAwaitValue()
        Mockito.verify(mainRepository).uploadStatus
        assertNotNull(actualUploadResponse)
        assertEquals(expectedUploadResponse.value, actualUploadResponse)
    }



//    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
//    @Test
//    fun `verify getStory is working and should not return null`() = runTest {
//        val noopListUpdateCallback = NoopListCallback()
//        val dummyStoriesUser = generateDummyStoriesUser()
//        val data = PagedTestDataSources.snapshot(dummyStoriesUser)
//        val story = MutableLiveData<PagingData<DetailPostStory>>()
//        val token = "tokennya"
//        story.value = data
//        `when`(mainRepository.getPagingStories(token)).thenReturn(story)
//        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()
//
//        val differ = AsyncPagingDataDiffer(
//            diffCallback = ListStoryAdapt.StoryDetailDiffCallback(),
//            updateCallback = noopListUpdateCallback,
//            mainDispatcher = Dispatchers.Unconfined,
//            workerDispatcher = Dispatchers.Unconfined,
//        )
//        differ.submitData(actualData)
//
//        advanceUntilIdle()
//        assertNotNull(differ.snapshot())
//        assertEquals(dummyStoriesUser.size, differ.snapshot().size)
//        assertEquals(dummyStoriesUser[0], differ.snapshot()[0])
//    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `verify getStory is working and should not return null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStoriesUser = generateDummyStoriesUser()
        val data = PagedTestDataSources.snapshot(dummyStoriesUser)
        val story = MutableLiveData<PagingData<DetailPostStory>>()
        val token = "tokennya"
        story.value = data
        `when`(mainRepository.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

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


//    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
//    @Test
//    fun `when getStory is empty should not return null`() = runTest {
//        val noopListUpdateCallback = NoopListCallback()
//        val data = PagedTestDataSources.snapshot(listOf())
//        val story = MutableLiveData<PagingData<DetailPostStory>>()
//        val token = "tokennya"
//        story.value = data
//        `when`(mainRepository.getPagingStories(token)).thenReturn(story)
//        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()
//
//        val differ = AsyncPagingDataDiffer(
//            diffCallback = ListStoryAdapt.StoryDetailDiffCallback(),
//            updateCallback = noopListUpdateCallback,
//            workerDispatcher = Dispatchers.Main,
//        )
//        differ.submitData(actualData)
//
//        advanceUntilIdle()
//        assertNotNull(differ.snapshot())
//        assertTrue(differ.snapshot().isEmpty())
//        print(differ.snapshot().size)
//    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `when getStory is empty should not return null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagedTestDataSources.snapshot(listOf())
        val story = MutableLiveData<PagingData<DetailPostStory>>()
        val token = "tokennya"
        story.value = data
        `when`(mainRepository.getPagingStories(token)).thenReturn(story)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

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
        assertTrue(differ.snapshot().isEmpty())
        print(differ.snapshot().size)
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

//    @Test
//    fun `when login response should return the right data and not null`() {
//        val expectedLoginResponse = "Login Successfully"
//        val expectedLiveData = MutableLiveData<String>().apply { value = expectedLoginResponse }
//
//        `when`(mainRepository.loginStatus).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualLoginResponse = mainViewModel.isLoggingin.getOrAwaitValue()
//
//        assertNotNull(actualLoginResponse)
//        assertEquals(expectedLoginResponse, actualLoginResponse)
//    }


    @Test
    fun `when login response should return the right data and not null`() {
        val expectedLoginResponse = MutableLiveData<String>()
        expectedLoginResponse.value = "Login Successfully"

        `when`(mainRepository.loginStatus).thenReturn(expectedLoginResponse)

        val actualLoginResponse = mainViewModel.isLoggingin.getOrAwaitValue()

        Mockito.verify(mainRepository).loginStatus
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLoginResponse.value, actualLoginResponse)
    }

//    @Test
//    fun `when login loading should return the right data and not null`() {
//        val expectedLoadingResponse = true
//        val expectedLiveData = MutableLiveData<Boolean>().apply { value = expectedLoadingResponse }
//
//        `when`(mainRepository.isLoad).thenReturn(expectedLiveData)
//
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()
//
//        assertNotNull(actualLoadingResponse)
//        assertEquals(expectedLoadingResponse, actualLoadingResponse)
//    }

    @Test
    fun `when login loading should return the right data and not null`() {
        val expectedLoadingResponse = MutableLiveData<Boolean>()
        expectedLoadingResponse.value = true

        `when`(mainRepository.isLoad).thenReturn(expectedLoadingResponse)

        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoad
        assertNotNull(actualLoadingResponse)
        assertEquals(expectedLoadingResponse.value, actualLoadingResponse)
    }

//    @Test
//    fun `when login should return the right data and not null`() {
//        val dummyLoginResponse = generateDummyLoginResponse()
//        val expectedLiveData = MutableLiveData<ResponseLogin>().apply { value = dummyLoginResponse }
//
//        `when`(mainRepository.authenticatedUser).thenReturn(expectedLiveData)
//
//        val listStoryVM = ListStoryVM(mainRepository)
//
//        val actualLoginResponse = listStoryVM.userlogin.getOrAwaitValue()
//
//        assertNotNull(actualLoginResponse)
//        assertEquals(dummyLoginResponse, actualLoginResponse)
//    }


    @Test
    fun `when login should return the right data and not null`() {
        val dummyLoginResponse = generateDummyLoginResponse()

        val expectedLoginResponse = MutableLiveData<ResponseLogin>()
        expectedLoginResponse.value = dummyLoginResponse

        `when`(mainRepository.authenticatedUser).thenReturn(expectedLoginResponse)

        val actualLoginResponse = mainViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(mainRepository).authenticatedUser
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLoginResponse.value, actualLoginResponse)
    }

//    @Test
//    fun `verify getResponseLogin function is working`() {
//        val dummyLoginUser = generateDummyLoginUser()
//        val dummyLoginResponse = generateDummyLoginResponse()
//        val expectedLiveData = MutableLiveData<ResponseLogin>().apply { value = dummyLoginResponse }
//
//        val expectedLoginResponse = MutableLiveData<ResponseLogin>()
//        expectedLoginResponse.value = dummyLoginResponse
//
//        mainViewModel.login(dummyLoginUser)
//
//        `when`(mainRepository.authenticatedUser).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualData = mainViewModel.userlogin.getOrAwaitValue()
//
//        assertNotNull(actualData)
//        assertEquals(dummyLoginResponse, actualData)
//    }


    @Test
    fun `verify getResponseLogin function is working`() {
        val dummyLoginUser = generateDummyLoginUser()
        val dummyLoginResponse = generateDummyLoginResponse()

        val expectedLoginResponse = MutableLiveData<ResponseLogin>()
        expectedLoginResponse.value = dummyLoginResponse

        mainRepository.getResponseLogin(dummyLoginUser)

        Mockito.verify(mainRepository).getResponseLogin(dummyLoginUser)

        `when`(mainRepository.authenticatedUser).thenReturn(expectedLoginResponse)

        val actualData = mainViewModel.userlogin.getOrAwaitValue()

        Mockito.verify(mainRepository).authenticatedUser
        assertNotNull(expectedLoginResponse)
        assertEquals(expectedLoginResponse.value, actualData)
    }

//    @Test
//    fun `when register response should return the right data and not null`() {
//        val expectedRegisterResponse = "Registration Account Successfully Created"
//        val expectedLiveData = MutableLiveData<String>().apply { value = expectedRegisterResponse }
//
//        `when`(mainRepository.registerStatus).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualRegisterResponse = mainViewModel.isRegistering.getOrAwaitValue()
//
//        assertNotNull(actualRegisterResponse)
//        assertEquals(expectedRegisterResponse, actualRegisterResponse)
//    }

    @Test
    fun `when register response should return the right data and not null`() {
        val expectedRegisterResponse = MutableLiveData<String>()
        expectedRegisterResponse.value = "Registration Account Successfully Created"

        `when`(mainRepository.registerStatus).thenReturn(expectedRegisterResponse)

        val actualRegisterResponse = mainViewModel.isRegistering.getOrAwaitValue()

        Mockito.verify(mainRepository).registerStatus
        assertNotNull(actualRegisterResponse)
        assertEquals(expectedRegisterResponse.value, actualRegisterResponse)
    }

//    @Test
//    fun `when register loading should return the right data and not null`() {
//        val expectedLoadingResponse = true
//        val expectedLiveData = MutableLiveData<Boolean>().apply { value = expectedLoadingResponse }
//
//        `when`(mainRepository.isLoad).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()
//
//        assertNotNull(actualLoadingResponse)
//        assertEquals(expectedLoadingResponse, actualLoadingResponse)
//    }

    @Test
    fun `when register loading should return the right data and not null`() {
        val expectedLoadingResponse = MutableLiveData<Boolean>()
        expectedLoadingResponse.value = true

        `when`(mainRepository.isLoad).thenReturn(expectedLoadingResponse)

        val actualLoadingResponse = mainViewModel.isLoad.getOrAwaitValue()

        Mockito.verify(mainRepository).isLoad
        assertNotNull(actualLoadingResponse)
        assertEquals(expectedLoadingResponse.value, actualLoadingResponse)
    }

//    @Test
//    fun `verify getResponseRegister function is working`() {
//        val expectedRegisterResponse = "Registration Account Successfully Created"
//        val expectedLiveData = MutableLiveData<String>().apply { value = expectedRegisterResponse }
//
//        `when`(mainRepository.registerStatus).thenReturn(expectedLiveData)
//        val mainViewModel = ListStoryVM(mainRepository)
//
//        val actualRegisterResponse = mainViewModel.isRegistering.getOrAwaitValue()
//
//        assertNotNull(actualRegisterResponse)
//        assertEquals(expectedRegisterResponse, actualRegisterResponse)
//    }


    @Test
    fun `verify getResponseRegister function is working`() {
        val dummyRegisterUser = generateDummyRegisterUser()
        val expectedRegisterResponse = MutableLiveData<String>()
        expectedRegisterResponse.value = "Registration Account Successfully Created"

        mainRepository.getResponseRegister(dummyRegisterUser)

        Mockito.verify(mainRepository).getResponseRegister(dummyRegisterUser)

        `when`(mainRepository.registerStatus).thenReturn(expectedRegisterResponse)

        val actualRegisterResponse = mainViewModel.isRegistering.getOrAwaitValue()

        Mockito.verify(mainRepository).registerStatus
        assertNotNull(actualRegisterResponse)
        assertEquals(expectedRegisterResponse.value, actualRegisterResponse)
    }
}