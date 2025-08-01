package com.eranio.efficientjanitor.domain

import com.eranio.efficientjanitor.util.Constants.MAX_BAG_WEIGHT
import javax.inject.Inject

class TripsCalculator @Inject constructor() {

    fun calculateTrips(weights: List<Double>): List<List<Double>> {
        val sorted = weights.sortedDescending().toMutableList()
        val trips = mutableListOf<List<Double>>()

        while (sorted.isNotEmpty()) {
            val trip = mutableListOf<Double>()
            var remaining = MAX_BAG_WEIGHT

            val iterator = sorted.iterator()
            while (iterator.hasNext()) {
                val w = iterator.next()
                if (w <= remaining) {
                    trip.add(w)
                    remaining -= w
                    iterator.remove()
                }
            }

            trips.add(trip)
        }

        return trips
    }
}