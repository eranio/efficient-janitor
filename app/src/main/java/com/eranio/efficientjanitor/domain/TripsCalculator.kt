package com.eranio.efficientjanitor.domain

import com.eranio.efficientjanitor.util.Constants.MAX_BAG_WEIGHT
import com.eranio.efficientjanitor.util.Constants.MIN_BAG_WEIGHT
import javax.inject.Inject

class TripsCalculator @Inject constructor() {

    fun calculateTrips(weights: List<Double>): List<List<Double>> {
        require(weights.all { it in MIN_BAG_WEIGHT..MAX_BAG_WEIGHT }) {
            "Bags must all be within the valid weight range"
        }

        val sortedBags = weights.sortedDescending().toMutableList()
        val trips = mutableListOf<MutableList<Double>>()

        while (sortedBags.isNotEmpty()) {
            val trip = mutableListOf<Double>()
            var remaining = MAX_BAG_WEIGHT

            for (weight in sortedBags.toList()) {
                if (weight <= remaining) {
                    trip.add(weight)
                    remaining -= weight
                    sortedBags.remove(weight)
                }
            }

            trips.add(trip)
        }

        return trips
    }
}