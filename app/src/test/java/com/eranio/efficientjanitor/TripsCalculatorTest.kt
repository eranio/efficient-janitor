package com.eranio.efficientjanitor

import com.eranio.efficientjanitor.domain.TripsCalculator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class TripsCalculatorTest {
    private lateinit var calculator: TripsCalculator

    @Before
    fun setup() {
        calculator = TripsCalculator()
    }

    @Test
    fun `bags that fit exactly into trips of 3kg`() {
        val result = calculator.calculateTrips(listOf(1.5, 1.5, 2.0, 1.0))
        assertEquals(2, result.size)
        assertTrue(result.any { it.sum() == 3.0 })
        assertTrue(result.all { it.sum() <= 3.0 })
    }

    @Test
    fun `bags that require separate trips due to size`() {
        val result = calculator.calculateTrips(listOf(3.0, 2.9, 2.8))
        assertEquals(3, result.size)
        assertTrue(result.all { it.size == 1 })
    }

    @Test
    fun `multiple combinations, check trip count and content`() {
        val input = listOf(1.5, 1.6, 1.0, 2.0, 1.01)
        val result = calculator.calculateTrips(input)

        assertTrue(result.all { it.sum() <= 3.0 })
        val flat = result.flatten().sorted()
        val expected = input.sorted()
        assertEquals(expected, flat)
    }
}