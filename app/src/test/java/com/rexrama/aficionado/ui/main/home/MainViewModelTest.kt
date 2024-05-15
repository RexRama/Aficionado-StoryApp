package com.rexrama.aficionado.ui.main.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.rexrama.aficionado.adapter.StoryAdapter
import com.rexrama.aficionado.data.local.repo.StoryRepo
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.utils.DummyData
import com.rexrama.aficionado.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(null, storyRepo)  // Use real object for ViewModel, mock the repository
    }

    private val dummyStories = DummyData.generateDummyStoryEntity(10)
    private val dummyEmptyStories = DummyData.generateDummyStoryEntity(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Test if get stories successfully loaded
    @Test
    fun `when Get All Stories Success`() = runTest {

        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = PagingData.from(dummyStories)

        Mockito.`when`(storyRepo.getStories()).thenReturn(expectedStory)

        val actualStory = mainViewModel.fetchStories().getOrAwaitValue()
        Mockito.verify(storyRepo).getStories()

        // Convert PagingData Stream
        val diff = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )
        diff.submitData(actualStory)

        // Check if data not null
        Assert.assertNotNull(actualStory)

        // Check if size match
        Assert.assertEquals(dummyStories.size, diff.snapshot().size)

        // Check if the first data match
        Assert.assertEquals(dummyStories[0], diff.snapshot()[0])
    }

    // Test if there are no stories data
    @Test
    fun `when There Are No Stories`() = runTest {

        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = PagingData.from(dummyEmptyStories)

        Mockito.`when`(storyRepo.getStories()).thenReturn(expectedStory)

        val actualStory = mainViewModel.fetchStories().getOrAwaitValue()
        Mockito.verify(storyRepo).getStories()

        // Convert PagingData Stream
        val diff = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )
        diff.submitData(actualStory)

        // Check if data size is 0
        Assert.assertEquals(0, diff.snapshot().size)
    }

    // list update callback for PagingData size
    private val updateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }
}
