package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports WHERE isDeleted = 0 ORDER BY isPinned DESC, timestamp DESC")
    fun getAllActiveReports(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE isDeleted = 1 ORDER BY timestamp DESC")
    fun getTrashReports(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE id = :id LIMIT 1")
    suspend fun getReportById(id: Long): Report?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report): Long

    @Update
    suspend fun updateReport(report: Report)

    @Query("UPDATE reports SET isDeleted = :isDeleted WHERE id = :id")
    suspend fun setDeletedStatus(id: Long, isDeleted: Boolean)

    @Query("UPDATE reports SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinnedStatus(id: Long, isPinned: Boolean)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM reports WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM reports")
    suspend fun deleteAll()

    @Query("SELECT * FROM reports")
    suspend fun getAllReportsSync(): List<Report>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<Report>): List<Long>

    @Query("SELECT COUNT(*) FROM reports WHERE isDeleted = 0")
    fun countActiveReports(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reports WHERE isDeleted = 1")
    fun countTrashReports(): Flow<Int>
}
