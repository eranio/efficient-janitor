package com.eranio.efficientjanitor.viewmodel

import app.cash.turbine.test
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.domain.BagRepository
import com.eranio.efficientjanitor.domain.TripsCalculator
import com.eranio.efficientjanitor.util.Constants.MAX_BAG_WEIGHT
import com.eranio.efficientjanitor.util.Constants.MIN_BAG_WEIGHT
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class JanitorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testBagsFlow = MutableStateFlow<List<Double>>(emptyList())

    private lateinit var bagRepository: BagRepository
    private lateinit var tripsCalculator: TripsCalculator
    private lateinit var viewModel: JanitorViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        bagRepository = mockk(relaxed = true)
        every { bagRepository.bagsFlow } returns testBagsFlow
        tripsCalculator = mockk(relaxed = true)
        viewModel = JanitorViewModel(bagRepository, tripsCalculator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAddBagClicked with valid weight adds bag`() = runTest {
        val input = "2.0"

        viewModel.onAddBagClicked(input)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { bagRepository.addBag(2.0) }
    }

    @Test
    fun `onAddBagClicked with non-numeric input emits snackbar`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onAddBagClicked("abc")
            assertEquals(UiEvent.ShowSnackbar(R.string.cant_be_empty, emptyList()), awaitItem())
        }
    }

    @Test
    fun `onAddBagClicked with out-of-range weight emits snackbar`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onAddBagClicked("10.0")
            assertEquals(
                UiEvent.ShowSnackbar(R.string.invalid_weight_range, listOf(MIN_BAG_WEIGHT, MAX_BAG_WEIGHT)),
                awaitItem()
            )
        }
    }

    @Test
    fun `onClearBagsClicked clears repository and emits success`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onClearBagsClicked()
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { bagRepository.clearBags() }
            assertEquals(UiEvent.ClearSuccess, awaitItem())
        }
    }

    @Test
    fun `onRemoveBag calls deleteBag on repository`() = runTest {
        viewModel.onRemoveBag(1.0)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { bagRepository.deleteBag(1.0) }
    }

    @Test
    fun `observeBags updates UI state when repository emits`() = runTest {
        val sampleBags = listOf(1.0, 2.0)
        testBagsFlow.value = sampleBags
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sampleBags, state.bags)
        }
    }

    @Test
    fun `onCalculateClicked updates trips in UI state`() = runTest {
        val bags = listOf(1.0, 2.0)
        val trips = listOf(listOf(1.0, 2.0))
        every { tripsCalculator.calculateTrips(bags) } returns trips

        // Emit new list to repository flow
        testBagsFlow.value = bags

        // Let the ViewModel update UI state before calculate is called
        advanceUntilIdle()

        // Now run the calculation
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(trips, state.trips)
        }
    }
}
