package com.eranio.efficientjanitor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bags")
data class BagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Double
)