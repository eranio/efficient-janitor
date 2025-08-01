package com.eranio.efficientjanitor.domain

import javax.inject.Inject

class TripsCalculator @Inject constructor() {

    fun calculateTrips(weights: List<Double>): List<List<Double>> {
        val sorted = weights.sortedDescending().toMutableList()
        val trips = mutableListOf<List<Double>>()

        while (sorted.isNotEmpty()) {
            val trip = mutableListOf<Double>()
            var remaining = 3.0

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