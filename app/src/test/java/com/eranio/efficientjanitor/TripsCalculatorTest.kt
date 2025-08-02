package com.eranio.efficientjanitor

import com.eranio.efficientjanitor.domain.TripsCalculator
import com.eranio.efficientjanitor.util.Constants.MAX_BAG_WEIGHT
import com.eranio.efficientjanitor.util.Constants.MIN_BAG_WEIGHT
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class TripsCalculatorTest {
    private lateinit var calculator: TripsCalculator

    @Before
    fun setup() {
        calculator = TripsCalculator()
    }

    @Test
    fun `bags that fit exactly into trips of 3kg`() {
        val result = calculator.calculateTrips(listOf(1.5, 1.5, 1.9, 1.1))
        assertEquals(2, result.size)
        assertTrue(result.any { it.sum() == MAX_BAG_WEIGHT })
        assertTrue(result.all { it.sum() <= MAX_BAG_WEIGHT })
    }

    @Test
    fun `bags that require separate trips due to size`() {
        val result = calculator.calculateTrips(listOf(3.0, 2.9, 2.8))
        assertEquals(3, result.size)
        assertTrue(result.all { it.size == 1 })
    }

    @Test
    fun `multiple combinations, check trip count and content`() {
        val input = listOf(1.5, 1.6, 1.1, 1.9, 1.01)
        val result = calculator.calculateTrips(input)

        assertTrue(result.all { it.sum() <= MAX_BAG_WEIGHT })
        val flat = result.flatten().sorted()
        val expected = input.sorted()
        assertEquals(expected, flat)
    }

    @Test
    fun `throws exception if bag weight is below minimum`() {
        val invalidBags = listOf(0.1, 1.0, 2.0)

        assertFailsWith<IllegalArgumentException> {
            calculator.calculateTrips(invalidBags)
        }
    }

    @Test
    fun `throws exception if bag weight is above maximum`() {
        val invalidBags = listOf(1.0, 2.5, 3.1)

        assertFailsWith<IllegalArgumentException> {
            calculator.calculateTrips(invalidBags)
        }
    }

    @Test
    fun `does not throw if all weights are within range`() {
        val validBags = listOf(MIN_BAG_WEIGHT, 1.5, MAX_BAG_WEIGHT)

        try {
            calculator.calculateTrips(validBags)
            // success: continue
        } catch (e: Exception) {
            throw AssertionError("Expected no exception, but got: ${e.message}")
        }
    }
}