package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val observerName: String = "",
    val sector: String = "A",
    val fieldNumber: String = "01",
    val observationDate: String = "",
    val savedDate: String = "",
    val specialNotes: String = "",
    val samplesJson: String = "",
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
