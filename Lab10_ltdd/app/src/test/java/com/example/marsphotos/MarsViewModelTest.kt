package com.example.marsphotos

import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.fake.FakeDataSource
import com.example.marsphotos.fake.FakeNetworkMarsPhotosRepository
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.rules.TestDispatcherRule
import com.example.marsphotos.ui.screens.MarsUiState
import com.example.marsphotos.ui.screens.MarsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MarsViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() = runTest {
        val marsViewModel = MarsViewModel(
            marsPhotosRepository = FakeNetworkMarsPhotosRepository()
        )
        assertEquals(
            MarsUiState.Success(FakeDataSource.photosList),
            marsViewModel.marsUiState
        )
    }

    @Test
    fun marsViewModel_getMarsPhotos_verifySuccessPhotoList() = runTest {
        val marsViewModel = MarsViewModel(
            marsPhotosRepository = FakeNetworkMarsPhotosRepository()
        )
        val state = marsViewModel.marsUiState
        assertTrue(state is MarsUiState.Success)
        assertEquals(FakeDataSource.photosList, (state as MarsUiState.Success).photos)
    }

    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateError() = runTest {
        val marsViewModel = MarsViewModel(
            marsPhotosRepository = object : MarsPhotosRepository {
                override suspend fun getMarsPhotos(): List<MarsPhoto> {
                    throw IOException("Network error")
                }
            }
        )
        assertEquals(MarsUiState.Error, marsViewModel.marsUiState)
    }

    @Test
    fun marsViewModel_retry_callsGetMarsPhotosAndUpdatesUiState() = runTest {
        var shouldFail = true
        var fetchCount = 0
        val repository = object : MarsPhotosRepository {
            override suspend fun getMarsPhotos(): List<MarsPhoto> {
                fetchCount++
                if (shouldFail) {
                    throw IOException("Network error")
                }
                return FakeDataSource.photosList
            }
        }
        val marsViewModel = MarsViewModel(marsPhotosRepository = repository)
        assertEquals(MarsUiState.Error, marsViewModel.marsUiState)
        assertEquals(1, fetchCount)

        shouldFail = false
        marsViewModel.getMarsPhotos()
        assertEquals(MarsUiState.Success(FakeDataSource.photosList), marsViewModel.marsUiState)
        assertEquals(2, fetchCount)
    }

    @Test
    fun marsViewModel_loading_verifyMarsUiStateLoading() = runTest {
        val standardDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(standardDispatcher)
        try {
            val marsViewModel = MarsViewModel(FakeNetworkMarsPhotosRepository())
            assertEquals(MarsUiState.Loading, marsViewModel.marsUiState)
            testScheduler.advanceUntilIdle()
            assertEquals(MarsUiState.Success(FakeDataSource.photosList), marsViewModel.marsUiState)
        } finally {
            Dispatchers.setMain(testDispatcher.testDispatcher)
        }
    }
}

