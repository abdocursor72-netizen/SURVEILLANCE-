package com.example.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.Report
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val appTitle: String,
    val appVersion: String,
    val createdAtFormatted: String,
    val createdAtTimestamp: Long,
    val observerName: String,
    val totalReportsCount: Int,
    val activeReportsCount: Int,
    val trashReportsCount: Int,
    val registeredSamplesCount: Int,
    val reports: List<Report>,
    val registeredSamples: List<SampleItem>,
    val preferences: Map<String, String>,
    val fileSizeFormatted: String = ""
)

data class LocalBackupFileItem(
    val file: File,
    val fileName: String,
    val formattedDate: String,
    val sizeText: String,
    val reportsCount: Int,
    val samplesCount: Int
)

object BackupManager {

    private const val IDENTIFIER = "SF_SURVEILLANCE_BACKUP"
    private const val BACKUP_DIR_NAME = "sf_backups"

    /**
     * Build the structured JSON string for backup
     */
    fun createBackupJson(
        reports: List<Report>,
        registeredSamples: List<SampleItem>,
        observerName: String,
        prefsMap: Map<String, String>
    ): String {
        val root = JSONObject()
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val dateFormatted = sdf.format(Date(now))

        root.put("app_identifier", IDENTIFIER)
        root.put("app_title", AppPreferences.APP_TITLE)
        root.put("app_version", AppPreferences.APP_VERSION)
        root.put("backup_version", 1)
        root.put("created_at_timestamp", now)
        root.put("created_at_formatted", dateFormatted)
        root.put("observer_name", observerName)

        val activeCount = reports.count { !it.isDeleted }
        val trashCount = reports.count { it.isDeleted }

        val statsObj = JSONObject()
        statsObj.put("total_reports", reports.size)
        statsObj.put("active_reports", activeCount)
        statsObj.put("trash_reports", trashCount)
        statsObj.put("registered_samples", registeredSamples.size)
        root.put("stats", statsObj)

        val prefsObj = JSONObject()
        prefsMap.forEach { (k, v) ->
            prefsObj.put(k, v)
        }
        root.put("preferences", prefsObj)

        // Registered Samples
        val samplesArray = JSONArray()
        registeredSamples.forEach { s ->
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("category", s.category.name)
            sObj.put("nameArabic", s.nameArabic)
            sObj.put("nameScientific", s.nameScientific)
            sObj.put("value", s.value)
            sObj.put("notes", s.notes)
            samplesArray.put(sObj)
        }
        root.put("registered_samples", samplesArray)

        // Reports
        val reportsArray = JSONArray()
        reports.forEach { r ->
            val rObj = JSONObject()
            rObj.put("sector", r.sector)
            rObj.put("fieldNumber", r.fieldNumber)
            rObj.put("observerName", r.observerName)
            rObj.put("observationDate", r.observationDate)
            rObj.put("savedDate", r.savedDate)
            rObj.put("specialNotes", r.specialNotes)
            rObj.put("samplesJson", r.samplesJson)
            rObj.put("isPinned", r.isPinned)
            rObj.put("isDeleted", r.isDeleted)
            rObj.put("timestamp", r.timestamp)
            rObj.put("imageUri", r.imageUri)
            rObj.put("diseaseNote", r.diseaseNote)
            reportsArray.put(rObj)
        }
        root.put("reports", reportsArray)

        return root.toString(2)
    }

    /**
     * Parse and validate backup JSON
     */
    fun parseBackupJson(jsonString: String): Result<BackupMetadata> {
        return try {
            val root = JSONObject(jsonString)
            val identifier = root.optString("app_identifier", "")
            if (identifier != IDENTIFIER && !root.has("reports")) {
                return Result.failure(IllegalArgumentException("الملف المحدد ليس ملف نسخة احتياطية صالح لتطبيق ${AppPreferences.APP_TITLE}"))
            }

            val appTitle = root.optString("app_title", AppPreferences.APP_TITLE)
            val appVersion = root.optString("app_version", "1.0")
            val createdAtFormatted = root.optString("created_at_formatted", "غير محدد")
            val createdAtTimestamp = root.optLong("created_at_timestamp", System.currentTimeMillis())
            val observerName = root.optString("observer_name", "")

            // Parse preferences
            val prefsMap = mutableMapOf<String, String>()
            val prefsObj = root.optJSONObject("preferences")
            if (prefsObj != null) {
                val keys = prefsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    prefsMap[k] = prefsObj.optString(k, "")
                }
            }

            // Parse registered samples
            val samplesList = mutableListOf<SampleItem>()
            val samplesArray = root.optJSONArray("registered_samples")
            if (samplesArray != null) {
                for (i in 0 until samplesArray.length()) {
                    val sObj = samplesArray.optJSONObject(i) ?: continue
                    val catStr = sObj.optString("category", SampleCategory.VIRUS.name)
                    val cat = try {
                        SampleCategory.valueOf(catStr)
                    } catch (e: Exception) {
                        SampleCategory.VIRUS
                    }
                    samplesList.add(
                        SampleItem(
                            id = sObj.optString("id", "sample_$i"),
                            category = cat,
                            nameArabic = sObj.optString("nameArabic", ""),
                            nameScientific = sObj.optString("nameScientific", ""),
                            value = sObj.optString("value", "0"),
                            notes = sObj.optString("notes", "---")
                        )
                    )
                }
            }

            // Parse reports
            val reportsList = mutableListOf<Report>()
            val reportsArray = root.optJSONArray("reports")
            if (reportsArray != null) {
                for (i in 0 until reportsArray.length()) {
                    val rObj = reportsArray.optJSONObject(i) ?: continue
                    reportsList.add(
                        Report(
                            id = 0L, // will autogenerate or match
                            sector = rObj.optString("sector", "A"),
                            fieldNumber = rObj.optString("fieldNumber", "01"),
                            observerName = rObj.optString("observerName", ""),
                            observationDate = rObj.optString("observationDate", ""),
                            savedDate = rObj.optString("savedDate", ""),
                            specialNotes = rObj.optString("specialNotes", ""),
                            samplesJson = rObj.optString("samplesJson", "[]"),
                            isPinned = rObj.optBoolean("isPinned", false),
                            isDeleted = rObj.optBoolean("isDeleted", false),
                            timestamp = rObj.optLong("timestamp", System.currentTimeMillis()),
                            imageUri = rObj.optString("imageUri", ""),
                            diseaseNote = rObj.optString("diseaseNote", "")
                        )
                    )
                }
            }

            val activeCount = reportsList.count { !it.isDeleted }
            val trashCount = reportsList.count { it.isDeleted }

            Result.success(
                BackupMetadata(
                    appTitle = appTitle,
                    appVersion = appVersion,
                    createdAtFormatted = createdAtFormatted,
                    createdAtTimestamp = createdAtTimestamp,
                    observerName = observerName,
                    totalReportsCount = reportsList.size,
                    activeReportsCount = activeCount,
                    trashReportsCount = trashCount,
                    registeredSamplesCount = samplesList.size,
                    reports = reportsList,
                    registeredSamples = samplesList,
                    preferences = prefsMap
                )
            )
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("خطأ في قراءة ملف النسخة الاحتياطية: ${e.localizedMessage ?: "بيانات غير صالحة"}"))
        }
    }

    /**
     * Inspect file from URI selected by user
     */
    fun inspectBackupUri(context: Context, uri: Uri): Result<BackupMetadata> {
        return try {
            val stringBuilder = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append('\n')
                        line = reader.readLine()
                    }
                }
            }
            val json = stringBuilder.toString()
            val parsed = parseBackupJson(json)
            parsed.map { meta ->
                val sizeBytes = json.toByteArray().size
                val sizeFormatted = formatFileSize(sizeBytes.toLong())
                meta.copy(fileSizeFormatted = sizeFormatted)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inspect backup from a local File
     */
    fun inspectBackupFile(file: File): Result<BackupMetadata> {
        return try {
            val json = file.readText()
            val parsed = parseBackupJson(json)
            parsed.map { meta ->
                val sizeFormatted = formatFileSize(file.length())
                meta.copy(fileSizeFormatted = sizeFormatted)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save backup directly to an output stream (e.g. from ActivityResultContracts.CreateDocument)
     */
    fun writeBackupToUri(context: Context, uri: Uri, backupJson: String): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(backupJson.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            } ?: return Result.failure(Exception("تعذر فتح مسار التخزين للحفظ"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save a quick local backup in internal storage
     */
    fun saveLocalBackup(context: Context, backupJson: String): Result<File> {
        return try {
            val backupsDir = File(context.filesDir, BACKUP_DIR_NAME)
            if (!backupsDir.exists()) {
                backupsDir.mkdirs()
            }
            val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
            val fileName = "SF_Surveillance_Backup_${fileDateFormat.format(Date())}.json"
            val file = File(backupsDir, fileName)
            file.writeText(backupJson, Charsets.UTF_8)
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * List all local backups saved in the app's backup folder
     */
    fun getLocalBackups(context: Context): List<LocalBackupFileItem> {
        val backupsDir = File(context.filesDir, BACKUP_DIR_NAME)
        if (!backupsDir.exists() || !backupsDir.isDirectory) {
            return emptyList()
        }

        val files = backupsDir.listFiles { f -> f.isFile && f.extension.equals("json", ignoreCase = true) }
            ?: return emptyList()

        return files.sortedByDescending { it.lastModified() }.mapNotNull { file ->
            try {
                val json = file.readText()
                val root = JSONObject(json)
                val stats = root.optJSONObject("stats")
                val reportsCount = stats?.optInt("total_reports") ?: 0
                val samplesCount = stats?.optInt("registered_samples") ?: 0
                val dateStr = root.optString("created_at_formatted", "")
                val formattedDate = if (dateStr.isNotBlank()) dateStr else {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(file.lastModified()))
                }

                LocalBackupFileItem(
                    file = file,
                    fileName = file.name,
                    formattedDate = formattedDate,
                    sizeText = formatFileSize(file.length()),
                    reportsCount = reportsCount,
                    samplesCount = samplesCount
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Delete a local backup file
     */
    fun deleteLocalBackup(file: File): Boolean {
        return try {
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Share backup JSON via WhatsApp / Email / Drive
     */
    fun shareBackup(context: Context, backupJson: String): Result<Unit> {
        return try {
            val cacheBackupsDir = File(context.cacheDir, "shared_backups")
            if (!cacheBackupsDir.exists()) {
                cacheBackupsDir.mkdirs()
            }
            val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
            val fileName = "SF_Surveillance_Backup_${fileDateFormat.format(Date())}.json"
            val file = File(cacheBackupsDir, fileName)
            file.writeText(backupJson, Charsets.UTF_8)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "نسخة احتياطية ${AppPreferences.APP_TITLE}")
                putExtra(Intent.EXTRA_TEXT, "ملف نسخة احتياطية لبيانات المراقبة الزراعية ${AppPreferences.APP_TITLE} - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "مشاركة النسخة الاحتياطية عبر")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateDefaultFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH)
        return "SF_Surveillance_Backup_${sdf.format(Date())}.json"
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.ENGLISH, "%.1f KB", kb)
        val mb = kb / 1024.0
        return String.format(Locale.ENGLISH, "%.1f MB", mb)
    }
}
