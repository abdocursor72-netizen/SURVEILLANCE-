package com.example.ui.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateReleaseInfo(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val downloadUrl: String,
    val changelog: List<String>,
    val fileSizeBytes: Long = 0L,
    val isNewer: Boolean = true
)

data class LocalApkInfo(
    val file: File,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val appLabel: String,
    val fileSizeMb: Double,
    val isCompatible: Boolean
)

object AppUpdateManager {

    /**
     * Checks online endpoint (e.g. GitHub raw, cloud server) for version info.
     */
    suspend fun checkOnlineUpdate(
        customUrl: String = AppPreferences.DEFAULT_UPDATE_URL
    ): Result<UpdateReleaseInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = URL(customUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 7000
                readTimeout = 7000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "SF-Surveillance-App/${AppPreferences.APP_VERSION}")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(jsonStr)

                val version = root.optString("version", "1.5")
                val versionCode = root.optInt("versionCode", 15)
                val releaseDate = root.optString("releaseDate", "11-09-2026")
                val downloadUrl = root.optString("downloadUrl", "")
                val changelogList = mutableListOf<String>()
                val changelogArray = root.optJSONArray("changelog")
                if (changelogArray != null) {
                    for (i in 0 until changelogArray.length()) {
                        changelogList.add(changelogArray.getString(i))
                    }
                } else {
                    val rawNotes = root.optString("notes", "")
                    if (rawNotes.isNotBlank()) changelogList.add(rawNotes)
                }

                val currentCode = AppPreferences.CURRENT_VERSION_CODE
                val isNewer = versionCode > currentCode || isVersionGreater(version, AppPreferences.APP_VERSION)

                val info = UpdateReleaseInfo(
                    versionName = version,
                    versionCode = versionCode,
                    releaseDate = releaseDate,
                    downloadUrl = downloadUrl,
                    changelog = if (changelogList.isEmpty()) listOf("تحسينات عامة على الأداء والمزامنة الميدانية") else changelogList,
                    isNewer = isNewer
                )
                Result.success(info)
            } else {
                // Return default/fallback comparison info indicating current is latest or server unavailable
                Result.failure(Exception("كود استجابة الخادم: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Helper to compare semantic versions like 1.6 vs 1.5
     */
    private fun isVersionGreater(remote: String, local: String): Boolean {
        return try {
            val cleanRemote = remote.trim().removePrefix("v").removePrefix("V")
            val cleanLocal = local.trim().removePrefix("v").removePrefix("V")
            val rParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
            val lParts = cleanLocal.split(".").mapNotNull { it.toIntOrNull() }
            val maxLen = maxOf(rParts.size, lParts.size)
            for (i in 0 until maxLen) {
                val r = rParts.getOrElse(i) { 0 }
                val l = lParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Downloads an APK file from URL with progress reporting.
     */
    suspend fun downloadApk(
        downloadUrl: String,
        destinationFile: File,
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 25000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "SF-Surveillance-App/${AppPreferences.APP_VERSION}")
            }

            val fileLength = connection.contentLength
            val inputStream: InputStream = connection.inputStream
            val outputStream = FileOutputStream(destinationFile)

            val buffer = ByteArray(8192)
            var totalBytesRead = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (fileLength > 0) {
                    val progress = (totalBytesRead.toFloat() / fileLength.toFloat()).coerceIn(0f, 1f)
                    withContext(Dispatchers.Main) {
                        onProgress(progress)
                    }
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            withContext(Dispatchers.Main) {
                onProgress(1.0f)
            }

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inspects a local APK selected by the user via file picker.
     */
    suspend fun inspectLocalApk(context: Context, uri: Uri): Result<LocalApkInfo> = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            val tempApkFile = File(cacheDir, "picked_update_${System.currentTimeMillis()}.apk")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempApkFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(Exception("تعذر قراءة ملف التحديث المحدد"))

            val pm = context.packageManager
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageArchiveInfo(tempApkFile.absolutePath, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageArchiveInfo(tempApkFile.absolutePath, 0)
            }

            if (packageInfo == null) {
                return@withContext Result.failure(Exception("الملف المحدد ليس حزمة تطبيق أندرويد (APK) صالحة"))
            }

            val pName = packageInfo.packageName ?: ""
            val vName = packageInfo.versionName ?: "1.0"
            val vCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            packageInfo.applicationInfo?.let { appInfo ->
                appInfo.sourceDir = tempApkFile.absolutePath
                appInfo.publicSourceDir = tempApkFile.absolutePath
            }
            val label = packageInfo.applicationInfo?.loadLabel(pm)?.toString() ?: "SF-Surveillance"
            val sizeMb = tempApkFile.length().toDouble() / (1024.0 * 1024.0)

            // Verify package name matching or compatible
            val isCompatible = pName.isBlank() || pName == context.packageName || pName.contains("surveillance", ignoreCase = true) || pName.contains("example", ignoreCase = true)

            Result.success(
                LocalApkInfo(
                    file = tempApkFile,
                    packageName = pName,
                    versionName = vName,
                    versionCode = vCode,
                    appLabel = label,
                    fileSizeMb = String.format("%.2f", sizeMb).toDoubleOrNull() ?: sizeMb,
                    isCompatible = isCompatible
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Triggers the Android PackageInstaller to install the specified APK file.
     */
    fun installApk(context: Context, apkFile: File): Result<Unit> {
        return try {
            if (!apkFile.exists()) {
                return Result.failure(Exception("ملف التحديث غير موجود على الذاكرة"))
            }

            // Check permission to install unknown apps on Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                    return Result.failure(Exception("يرجى تفعيل صلاحية تثبيت التطبيقات غير المعروفة للتطبيق ثم إعادة المحاولة"))
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val apkUri = FileProvider.getUriForFile(context, authority, apkFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
