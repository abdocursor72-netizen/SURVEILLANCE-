package com.example.data.local

import com.example.data.model.Report
import com.example.data.model.SampleItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository(private val dao: ReportDao) {
    val activeReports: Flow<List<Report>> = dao.getAllActiveReports()
    val trashReports: Flow<List<Report>> = dao.getTrashReports()
    val activeCount: Flow<Int> = dao.countActiveReports()
    val trashCount: Flow<Int> = dao.countTrashReports()

    suspend fun getReportById(id: Long): Report? = dao.getReportById(id)

    suspend fun saveReport(report: Report): Long {
        return if (report.id == 0L) {
            dao.insertReport(report)
        } else {
            dao.updateReport(report)
            report.id
        }
    }

    suspend fun moveToTrash(id: Long) {
        dao.setDeletedStatus(id, true)
    }

    suspend fun restoreFromTrash(id: Long) {
        dao.setDeletedStatus(id, false)
    }

    suspend fun deletePermanently(id: Long) {
        dao.deletePermanently(id)
    }

    suspend fun togglePin(id: Long, currentPin: Boolean) {
        dao.setPinnedStatus(id, !currentPin)
    }

    suspend fun emptyTrash() {
        dao.emptyTrash()
    }

    suspend fun resetToFactoryDefaults() {
        dao.deleteAll()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val today = sdf.format(Date())
        val defaultSamples = SampleItem.getDefaultSamples()
        val jsonSamples = SampleItem.listToJson(defaultSamples)

        val report1 = Report(
            observerName = "ملاحظ رئيسي",
            sector = "a",
            fieldNumber = "5",
            observationDate = today,
            savedDate = today,
            specialNotes = "فحص ميداني أولي للقطاع a",
            samplesJson = jsonSamples,
            isPinned = false,
            isDeleted = false,
            timestamp = System.currentTimeMillis() - 60000
        )

        val report2 = Report(
            observerName = "ملاحظ رئيسي",
            sector = "A",
            fieldNumber = "5",
            observationDate = today,
            savedDate = today,
            specialNotes = "متابعة دورية لحالة النباتات",
            samplesJson = jsonSamples,
            isPinned = false,
            isDeleted = false,
            timestamp = System.currentTimeMillis() - 120000
        )

        dao.insertReport(report1)
        dao.insertReport(report2)
    }

    suspend fun getAllReportsSync(): List<Report> = dao.getAllReportsSync()

    suspend fun restoreReports(reports: List<Report>, replaceAll: Boolean) {
        if (replaceAll) {
            dao.deleteAll()
            // Reset IDs to 0 so Room re-generates autoincrement keys cleanly
            val cleanReports = reports.map { it.copy(id = 0L) }
            dao.insertAll(cleanReports)
        } else {
            val existing = dao.getAllReportsSync()
            val existingSignatures = existing.map { "${it.sector.trim().lowercase()}_${it.fieldNumber.trim()}_${it.observationDate.trim()}_${it.observerName.trim()}" }.toSet()
            val newReports = reports.filterNot { r ->
                val sig = "${r.sector.trim().lowercase()}_${r.fieldNumber.trim()}_${r.observationDate.trim()}_${r.observerName.trim()}"
                existingSignatures.contains(sig)
            }.map { it.copy(id = 0L) }
            
            if (newReports.isNotEmpty()) {
                dao.insertAll(newReports)
            }
        }
    }
}
