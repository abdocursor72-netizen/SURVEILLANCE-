package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ReportRepository
import com.example.data.model.Report
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import com.example.data.backup.BackupManager
import com.example.data.backup.BackupMetadata
import com.example.data.backup.LocalBackupFileItem
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScreenDestination {
    REPORTS_LIST,
    REPORT_EDITOR,
    SETTINGS,
    TRASH,
    SAMPLES_CATALOG,
    EXPORT_REPORT
}

enum class SortOrder {
    NEWEST,
    OLDEST,
    FIELD_NUMBER,
    SECTOR
}

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReportRepository
    val prefs: AppPreferences = AppPreferences(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ReportRepository(db.reportDao())
    }

    // Current navigation destination
    private val _currentScreen = MutableStateFlow(ScreenDestination.REPORTS_LIST)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Search query & sort order for reports list
    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.NEWEST)

    // Raw reports from DB
    private val rawReports = repository.activeReports
    val trashReports = repository.trashReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val activeCount = repository.activeCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )
    val trashCount = repository.trashCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Filtered & sorted active reports
    val refreshTrigger = MutableStateFlow(0L)
    val isAutoRefreshEnabled = MutableStateFlow(prefs.isAutoRefreshEnabled)
    val autoRefreshInterval = MutableStateFlow(prefs.autoRefreshIntervalSeconds)
    val isRefreshing = MutableStateFlow(false)
    val lastRefreshTime = MutableStateFlow(System.currentTimeMillis())
    val themeMode = MutableStateFlow(prefs.themeMode)

    fun setThemeMode(mode: String) {
        prefs.themeMode = mode
        themeMode.value = mode
    }

    private var autoRefreshJob: Job? = null

    val displayedReports: StateFlow<List<Report>> = combine(
        rawReports,
        searchQuery,
        sortOrder,
        refreshTrigger
    ) { reports, query, sort, _ ->
        var list = if (query.isBlank()) {
            reports
        } else {
            val q = query.trim().lowercase()
            reports.filter { r ->
                r.sector.lowercase().contains(q) ||
                r.fieldNumber.lowercase().contains(q) ||
                r.observerName.lowercase().contains(q) ||
                r.observationDate.lowercase().contains(q) ||
                r.specialNotes.lowercase().contains(q)
            }
        }

        when (sort) {
            SortOrder.NEWEST -> list.sortedWith(compareByDescending<Report> { it.isPinned }.thenByDescending { it.timestamp })
            SortOrder.OLDEST -> list.sortedWith(compareByDescending<Report> { it.isPinned }.thenBy { it.timestamp })
            SortOrder.FIELD_NUMBER -> list.sortedWith(compareByDescending<Report> { it.isPinned }.thenBy { it.fieldNumber })
            SortOrder.SECTOR -> list.sortedWith(compareByDescending<Report> { it.isPinned }.thenBy { it.sector })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Report Form State for Editor
    var editingReportId: Long = 0L
        private set
    val formObserverName = MutableStateFlow("")
    val formSector = MutableStateFlow("A")
    val formFieldNumber = MutableStateFlow("01")
    val formObservationDate = MutableStateFlow("")
    val formSavedDate = MutableStateFlow("")
    val formSpecialNotes = MutableStateFlow("")
    val formSamples = MutableStateFlow<List<SampleItem>>(emptyList())

    // Registered samples list (configured in Settings)
    val registeredSamples = MutableStateFlow<List<SampleItem>>(emptyList())

    init {
        val savedJson = prefs.customSamplesJson
        val initialList = if (savedJson.isNotBlank()) {
            val parsed = SampleItem.listFromJson(savedJson)
            if (parsed.isNotEmpty()) parsed else SampleItem.getDefaultSamples()
        } else {
            SampleItem.getDefaultSamples()
        }
        registeredSamples.value = initialList
        startAutoRefreshLoop()
    }

    fun startAutoRefreshLoop() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                val intervalSec = autoRefreshInterval.value.coerceAtLeast(3)
                delay(intervalSec * 1000L)
                if (isAutoRefreshEnabled.value) {
                    triggerRefresh()
                }
            }
        }
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        isAutoRefreshEnabled.value = enabled
        prefs.isAutoRefreshEnabled = enabled
        if (enabled) {
            triggerRefresh()
        }
    }

    fun setAutoRefreshInterval(seconds: Int) {
        val safeSeconds = seconds.coerceAtLeast(3)
        autoRefreshInterval.value = safeSeconds
        prefs.autoRefreshIntervalSeconds = safeSeconds
        startAutoRefreshLoop()
    }

    fun triggerRefresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            // Reload custom samples from prefs in case updated elsewhere
            val savedJson = prefs.customSamplesJson
            if (savedJson.isNotBlank()) {
                val parsed = SampleItem.listFromJson(savedJson)
                if (parsed.isNotEmpty()) {
                    registeredSamples.value = parsed
                }
            }
            refreshTrigger.value = System.currentTimeMillis()
            lastRefreshTime.value = System.currentTimeMillis()
            delay(350)
            isRefreshing.value = false
        }
    }

    fun addSample(arabicName: String, foreignName: String, category: SampleCategory) {
        val newItem = SampleItem(
            id = "custom_" + System.currentTimeMillis(),
            category = category,
            nameArabic = arabicName.trim(),
            nameScientific = foreignName.trim().ifBlank { arabicName.trim() },
            value = if (category == SampleCategory.VIRUS) "0" else "0/3",
            notes = "---"
        )
        val updated = registeredSamples.value + newItem
        registeredSamples.value = updated
        prefs.customSamplesJson = SampleItem.listToJson(updated)
    }

    fun deleteSample(sampleId: String) {
        val updated = registeredSamples.value.filterNot { it.id == sampleId }
        registeredSamples.value = updated
        prefs.customSamplesJson = SampleItem.listToJson(updated)
    }

    // Expanded accordion states in editor
    val isBasicInfoExpanded = MutableStateFlow(true)
    val isVirusesExpanded = MutableStateFlow(false)
    val isPestsExpanded = MutableStateFlow(false)
    val isBeneficialExpanded = MutableStateFlow(false)
    val isSpecialNotesExpanded = MutableStateFlow(false)

    private var previousScreen: ScreenDestination = ScreenDestination.REPORTS_LIST
    val exportReportTarget = MutableStateFlow<Report?>(null)

    fun navigateTo(destination: ScreenDestination) {
        if (destination != ScreenDestination.EXPORT_REPORT) {
            previousScreen = _currentScreen.value
        }
        _currentScreen.value = destination
    }

    fun openExportScreen(report: Report) {
        previousScreen = _currentScreen.value
        exportReportTarget.value = report
        _currentScreen.value = ScreenDestination.EXPORT_REPORT
    }

    fun openExportScreenLatest() {
        val latest = displayedReports.value.firstOrNull() ?: Report(
            fieldNumber = "5",
            sector = "BLa",
            observerName = prefs.defaultObserverName.ifBlank { "FOUAD EL..." },
            observationDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date()),
            samplesJson = SampleItem.listToJson(SampleItem.getDefaultSamples())
        )
        openExportScreen(latest)
    }

    fun navigateBackFromExport() {
        _currentScreen.value = previousScreen
    }

    fun startNewReport() {
        editingReportId = 0L
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val today = sdf.format(Date())
        formObserverName.value = prefs.defaultObserverName
        formSector.value = "A"
        formFieldNumber.value = "01"
        formObservationDate.value = today
        formSavedDate.value = today
        formSpecialNotes.value = ""
        formSamples.value = registeredSamples.value.map { it.copy() }

        // Accordion states
        isBasicInfoExpanded.value = true
        isVirusesExpanded.value = false
        isPestsExpanded.value = false
        isBeneficialExpanded.value = false
        isSpecialNotesExpanded.value = false

        _currentScreen.value = ScreenDestination.REPORT_EDITOR
    }

    fun editReport(report: Report) {
        editingReportId = report.id
        formObserverName.value = report.observerName
        formSector.value = report.sector
        formFieldNumber.value = report.fieldNumber
        formObservationDate.value = report.observationDate
        formSavedDate.value = report.savedDate
        formSpecialNotes.value = report.specialNotes

        val items = SampleItem.listFromJson(report.samplesJson)
        formSamples.value = if (items.isNotEmpty()) items else SampleItem.getDefaultSamples().map { it.copy() }

        isBasicInfoExpanded.value = true
        isVirusesExpanded.value = false
        isPestsExpanded.value = false
        isBeneficialExpanded.value = false
        isSpecialNotesExpanded.value = false

        _currentScreen.value = ScreenDestination.REPORT_EDITOR
    }

    fun updateSampleValue(sampleId: String, newValue: String) {
        val currentList = formSamples.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sampleId }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(value = newValue)
            formSamples.value = currentList
        }
    }

    fun cycleSampleSeverity(sampleId: String) {
        val currentList = formSamples.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sampleId }
        if (index != -1) {
            val item = currentList[index]
            val nextVal = when (item.value) {
                "0/3" -> "1/3"
                "1/3" -> "2/3"
                "2/3" -> "3/3"
                "3/3" -> "0/3"
                else -> {
                    // For number counts like "0", "1", "2"
                    val num = item.value.toIntOrNull() ?: 0
                    if (num >= 5) "0" else (num + 1).toString()
                }
            }
            currentList[index] = item.copy(value = nextVal)
            formSamples.value = currentList
        }
    }

    fun updateSampleNotes(sampleId: String, newNotes: String) {
        val currentList = formSamples.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == sampleId }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(notes = if (newNotes.isBlank()) "---" else newNotes.trim())
            formSamples.value = currentList
        }
    }

    fun saveCurrentReport(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val today = sdf.format(Date())
            val savedDate = if (formSavedDate.value.isNotBlank()) formSavedDate.value else today
            val obsDate = if (formObservationDate.value.isNotBlank()) formObservationDate.value else today

            val report = Report(
                id = editingReportId,
                observerName = formObserverName.value.trim(),
                sector = formSector.value.trim().ifBlank { "A" },
                fieldNumber = formFieldNumber.value.trim().ifBlank { "01" },
                observationDate = obsDate,
                savedDate = savedDate,
                specialNotes = formSpecialNotes.value.trim(),
                samplesJson = SampleItem.listToJson(formSamples.value),
                isPinned = false,
                isDeleted = false,
                timestamp = System.currentTimeMillis()
            )

            repository.saveReport(report)
            _currentScreen.value = ScreenDestination.REPORTS_LIST
            onSaved()
        }
    }

    fun togglePin(report: Report) {
        viewModelScope.launch {
            repository.togglePin(report.id, report.isPinned)
        }
    }

    fun deleteReport(report: Report) {
        viewModelScope.launch {
            repository.moveToTrash(report.id)
        }
    }

    fun restoreReport(report: Report) {
        viewModelScope.launch {
            repository.restoreFromTrash(report.id)
        }
    }

    fun deletePermanently(report: Report) {
        viewModelScope.launch {
            repository.deletePermanently(report.id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToFactoryDefaults()
        }
    }

    fun shareReport(context: Context, report: Report) {
        val items = SampleItem.listFromJson(report.samplesJson)
        val viruses = items.filter { it.category == SampleCategory.VIRUS }
        val pests = items.filter { it.category == SampleCategory.PEST }
        val beneficials = items.filter { it.category == SampleCategory.BENEFICIAL }

        val sb = StringBuilder()
        sb.append("📋 ${AppPreferences.APP_TITLE} Report\n")
        sb.append("═══════════════════════════\n")
        sb.append("🌾 الحقل (Field): ${report.fieldNumber}\n")
        sb.append("📍 القطاع (Secteur): ${report.sector}\n")
        sb.append("👤 الملاحظ (Observer): ${report.observerName.ifBlank { "غير محدد" }}\n")
        sb.append("📅 تاريخ الملاحظة: ${report.observationDate}\n")
        sb.append("💾 تاريخ الحفظ: ${report.savedDate}\n")
        sb.append("═══════════════════════════\n")

        if (viruses.isNotEmpty()) {
            sb.append("\n🦠 الفيروسات (Viruses):\n")
            viruses.forEach {
                sb.append(" • ${it.nameArabic} (${it.nameScientific}): ${it.value} | ملاحظات: ${it.notes}\n")
            }
        }

        if (pests.isNotEmpty()) {
            sb.append("\n🦗 الآفات (Pests):\n")
            pests.forEach {
                sb.append(" • ${it.nameArabic} (${it.nameScientific}): ${it.value} | ملاحظات: ${it.notes}\n")
            }
        }

        if (beneficials.isNotEmpty()) {
            sb.append("\n🛡️ الحشرات النافعة (Beneficials):\n")
            beneficials.forEach {
                sb.append(" • ${it.nameArabic} (${it.nameScientific}): ${it.value} | ملاحظات: ${it.notes}\n")
            }
        }

        if (report.specialNotes.isNotBlank()) {
            sb.append("\n📝 ملاحظات خاصة:\n${report.specialNotes}\n")
        }

        sb.append("\n---\nتم الإنشاء بواسطة ${AppPreferences.APP_TITLE}\n${AppPreferences.DEVELOPER_NAME}")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة التقرير عبر")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareAllReportsSummary(context: Context, reports: List<Report>) {
        val sb = StringBuilder()
        sb.append("📊 ${AppPreferences.APP_TITLE} - ملخص كافة التقارير\n")
        sb.append("عدد التقارير: ${reports.size}\n")
        sb.append("المطور: ${AppPreferences.DEVELOPER_NAME}\n\n")

        reports.forEachIndexed { idx, r ->
            sb.append("[${idx + 1}] القطاع: ${r.sector} | الحقل: ${r.fieldNumber} | التاريخ: ${r.observationDate}\n")
            if (r.specialNotes.isNotBlank()) {
                sb.append("    ملاحظة: ${r.specialNotes}\n")
            }
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "تصدير ملخص التقارير")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    suspend fun createBackupPayload(): String {
        val allReports = repository.getAllReportsSync()
        val samples = registeredSamples.value
        val observerName = prefs.defaultObserverName
        val prefsMap = mapOf(
            "default_observer_name" to prefs.defaultObserverName,
            "export_language" to prefs.exportLanguage,
            "theme_mode" to prefs.themeMode,
            "auto_refresh_enabled" to prefs.isAutoRefreshEnabled.toString(),
            "auto_refresh_interval" to prefs.autoRefreshIntervalSeconds.toString()
        )
        return BackupManager.createBackupJson(
            reports = allReports,
            registeredSamples = samples,
            observerName = observerName,
            prefsMap = prefsMap
        )
    }

    suspend fun restoreBackup(metadata: BackupMetadata, replaceAll: Boolean): Result<Int> {
        return try {
            repository.restoreReports(metadata.reports, replaceAll)

            if (replaceAll) {
                if (metadata.registeredSamples.isNotEmpty()) {
                    registeredSamples.value = metadata.registeredSamples
                    prefs.customSamplesJson = SampleItem.listToJson(metadata.registeredSamples)
                }
                if (metadata.observerName.isNotBlank()) {
                    prefs.defaultObserverName = metadata.observerName
                }
                metadata.preferences["export_language"]?.let { if (it.isNotBlank()) prefs.exportLanguage = it }
                metadata.preferences["theme_mode"]?.let { if (it.isNotBlank()) setThemeMode(it) }
            } else {
                // Merge samples
                if (metadata.registeredSamples.isNotEmpty()) {
                    val currentNames = registeredSamples.value.map { it.nameArabic.trim() }.toSet()
                    val newSamples = metadata.registeredSamples.filterNot { currentNames.contains(it.nameArabic.trim()) }
                    if (newSamples.isNotEmpty()) {
                        val merged = registeredSamples.value + newSamples
                        registeredSamples.value = merged
                        prefs.customSamplesJson = SampleItem.listToJson(merged)
                    }
                }
            }

            triggerRefresh()
            Result.success(metadata.reports.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveLocalBackup(context: Context): Result<File> {
        val payload = createBackupPayload()
        return BackupManager.saveLocalBackup(context, payload)
    }

    fun getLocalBackups(context: Context): List<LocalBackupFileItem> {
        return BackupManager.getLocalBackups(context)
    }

    fun deleteLocalBackup(file: File): Boolean {
        return BackupManager.deleteLocalBackup(file)
    }
}
