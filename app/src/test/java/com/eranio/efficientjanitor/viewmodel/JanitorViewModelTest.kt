package com.eranio.efficientjanitor.viewmodel

import app.cash.turbine.test
import com.eranio.efficientjanitor.R
import com.eranio.efficientjanitor.data.local.BagEntity
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
    private val testBagsFlow = MutableStateFlow<List<BagEntity>>(emptyList())

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
        val weight = 1.0
        val expectedEntity = BagEntity(weight = weight)

        viewModel.onRemoveBag(expectedEntity)
        advanceUntilIdle()

        coVerify { bagRepository.deleteBag(expectedEntity.id) }
    }

    @Test
    fun `observeBags updates UI state when repository emits`() = runTest {
        val sampleEntities = listOf(
            BagEntity(weight = 1.0),
            BagEntity(weight = 2.0)
        )
        val expectedWeights = sampleEntities

        testBagsFlow.value = sampleEntities
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals<List<Any>>(expectedWeights, state.bags)
        }
    }

    @Test
    fun `onCalculateClicked updates trips in UI state`() = runTest {
        val sampleEntities = listOf(
            BagEntity(weight = 1.0),
            BagEntity(weight = 2.0)
        )
        val weights = sampleEntities.map { it.weight.toDouble() }
        val trips: List<List<Double>> = listOf(listOf(1.0, 2.0))

        every { tripsCalculator.calculateTrips(weights) } returns trips

        // Emit new list to repository flow
        testBagsFlow.value = sampleEntities
        advanceUntilIdle()

        // Now run the calculation
        viewModel.onCalculateClicked()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals<List<Any>>(trips, state.trips.map { it.weights })
        }
    }
}
