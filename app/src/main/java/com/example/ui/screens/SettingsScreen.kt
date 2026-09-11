package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import com.example.data.backup.BackupManager
import com.example.data.backup.BackupMetadata
import com.example.data.backup.LocalBackupFileItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.update.AppUpdateManager
import com.example.ui.update.LocalApkInfo
import com.example.ui.update.UpdateReleaseInfo
import java.io.File
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.DialogProperties
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import com.example.ui.ReportViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.SurveillanceTopBar
import com.example.ui.theme.AccentBeneficialGreen
import com.example.ui.theme.AccentInfoBlue
import com.example.ui.theme.AccentPestOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AccentVirusRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GrayButtonBg
import com.example.ui.theme.GrayButtonBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsScreen(viewModel: ReportViewModel) {
    val context = LocalContext.current
    val trashReports by viewModel.trashReports.collectAsState()
    val trashCount by viewModel.trashCount.collectAsState()
    val allReports by viewModel.displayedReports.collectAsState()
    val registeredSamples by viewModel.registeredSamples.collectAsState()

    var showUserDialog by remember { mutableStateOf(false) }
    var showSamplesDialog by remember { mutableStateOf(false) }
    var showAddSampleDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showTrashDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAutoRefreshDialog by remember { mutableStateOf(false) }
    var showManualRefreshConfirmDialog by remember { mutableStateOf(false) }
    var showWhatsNewDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    val isAutoRefreshEnabled by viewModel.isAutoRefreshEnabled.collectAsState()
    val autoRefreshInterval by viewModel.autoRefreshInterval.collectAsState()

    var tempUserName by remember { mutableStateOf(viewModel.prefs.defaultObserverName) }
    var currentExportLang by remember { mutableStateOf(viewModel.prefs.exportLanguage) }
    val currentThemeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            SurveillanceTopBar(
                title = AppPreferences.APP_TITLE,
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.REPORTS_LIST) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Subheader: "الإعدادات" (Matching Screenshot 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الإعدادات",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Settings Items matching Screenshot 2
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. المستخدم (User)
                item {
                    SettingsCardItem(
                        title = "المستخدم",
                        subtitle = if (viewModel.prefs.defaultObserverName.isNotBlank()) viewModel.prefs.defaultObserverName else "لم يتم التحديد",
                        icon = Icons.Default.Person,
                        iconTint = AccentInfoBlue,
                        testTag = "settings_item_user",
                        onClick = {
                            tempUserName = viewModel.prefs.defaultObserverName
                            showUserDialog = true
                        }
                    )
                }

                // 2. العينات (Samples)
                item {
                    SettingsCardItem(
                        title = "العينات",
                        subtitle = "${registeredSamples.size} عينات مسجلة",
                        icon = Icons.Default.Inventory2,
                        iconTint = Color(0xFFF97316),
                        testTag = "settings_item_samples",
                        onClick = { showSamplesDialog = true }
                    )
                }

                // 3. التصدير (Export) - Custom expandable card matching user screenshots
                item {
                    ExportSettingsCard(
                        viewModel = viewModel
                    )
                }

                // 3.5. النسخ الاحتياطي (Backup & Restore)
                item {
                    SettingsCardItem(
                        title = "النسخ الاحتياطي",
                        subtitle = "حفظ واستعادة بيانات التقارير والعينات بأمان",
                        icon = Icons.Default.Backup,
                        iconTint = Color(0xFF0D9488),
                        testTag = "settings_item_backup",
                        onClick = { showBackupDialog = true }
                    )
                }

                // 4. المظهر (Appearance)
                item {
                    SettingsCardItem(
                        title = "المظهر",
                        subtitle = "الوضع: $currentThemeMode",
                        icon = Icons.Default.NightlightRound,
                        iconTint = Color(0xFF6366F1),
                        testTag = "settings_item_theme",
                        onClick = { showThemeDialog = true }
                    )
                }

                // 5. التحديث التلقائي (Auto-Refresh)
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showAutoRefreshDialog = true }
                            .testTag("settings_item_auto_refresh")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Switch for immediate toggle
                            Switch(
                                checked = isAutoRefreshEnabled,
                                onCheckedChange = { viewModel.setAutoRefreshEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EmeraldPrimary
                                ),
                                modifier = Modifier.testTag("settings_auto_refresh_switch")
                            )

                            // Right: Text + Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "التحديث التلقائي",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isAutoRefreshEnabled) {
                                            when (autoRefreshInterval) {
                                                5 -> "مفعّل (كل 5 ثوانٍ)"
                                                10 -> "مفعّل (كل 10 ثوانٍ)"
                                                30 -> "مفعّل (كل 30 ثانية)"
                                                60 -> "مفعّل (كل دقيقة)"
                                                else -> "مفعّل (كل $autoRefreshInterval ثانية)"
                                            }
                                        } else {
                                            "متوقف (انقر للضبط)"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = if (isAutoRefreshEnabled) EmeraldPrimary else TextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. المزيد (More - الجمع التلقائي والمعلومات)
                item {
                    SettingsCardItem(
                        title = "المزيد",
                        subtitle = "الجمع التلقائي والمعلومات",
                        icon = Icons.Default.AddCircle,
                        iconTint = AccentTeal,
                        testTag = "settings_item_more",
                        onClick = { showMoreDialog = true }
                    )
                }

                // 6. المهملات (Trash)
                item {
                    SettingsCardItem(
                        title = "المهملات",
                        subtitle = if (trashCount == 0) "سلة المهملات فارغة" else "$trashCount تقارير في سلة المهملات",
                        icon = Icons.Default.Delete,
                        iconTint = AccentVirusRed,
                        testTag = "settings_item_trash",
                        onClick = { showTrashDialog = true }
                    )
                }

                // 7. التهيئة (Reset)
                item {
                    SettingsCardItem(
                        title = "التهيئة",
                        subtitle = "إعادة ضبط المصنع ومسح البيانات",
                        icon = Icons.Default.Refresh,
                        iconTint = Color(0xFFF43F5E),
                        testTag = "settings_item_reset",
                        onClick = { showResetDialog = true }
                    )
                }

                // Developer Branding & Version Footer (matching user screenshot)
                item {
                    Spacer(modifier = Modifier.height(26.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo Icon Badge (using actual app launcher logo / icon)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_custom_1789078625195),
                                contentDescription = "شعار التطبيق",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // by ABDALKAYOUM  MOUSAID.
                        Text(
                            text = "by ABDALKAYOUM  MOUSAID.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp,
                                letterSpacing = 0.6.sp
                            ),
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 11-09-2026 • v1.0.12
                        Text(
                            text = "11-09-2026  •  v${AppPreferences.APP_VERSION}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Underlined row: ما الجديد في هاته النسخة • تحديث التطبيق
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("footer_links_row")
                        ) {
                            // "ما الجديد في هاته النسخة"
                            Text(
                                text = "ما الجديد في هاته النسخة",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline
                                ),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showWhatsNewDialog = true }
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .testTag("footer_whats_new_link")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // "تحديث التطبيق" with paper airplane icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showUpdateDialog = true }
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .testTag("footer_update_app_link")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تحديث التطبيق",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = TextDecoration.Underline
                                    ),
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. User Dialog
    if (showUserDialog) {
        AlertDialog(
            onDismissRequest = { showUserDialog = false },
            title = { Text("تحديد اسم الملاحظ الافتراضي", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "سيتم استخدام هذا الاسم تلقائياً عند إنشاء أي تقرير جديد:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempUserName,
                        onValueChange = { tempUserName = it },
                        placeholder = { Text("مثال: ملاحظ رئيسي") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_user_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.prefs.defaultObserverName = tempUserName.trim()
                        showUserDialog = false
                        Toast.makeText(context, "تم حفظ اسم المستخدم", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUserDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 1.5. Samples Dialog (نافذة العينات المفتوحة عند الضغط على العينات)
    if (showSamplesDialog) {
        val virusSamples = registeredSamples.filter { it.category == SampleCategory.VIRUS }
        val pestSamples = registeredSamples.filter { it.category == SampleCategory.PEST }
        val beneficialSamples = registeredSamples.filter { it.category == SampleCategory.BENEFICIAL }

        Dialog(onDismissRequest = { showSamplesDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header Row: Close on left, Title & Icon on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close icon in gray circle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showSamplesDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Title and Orange Inventory Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "العينات",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${registeredSamples.size} عينات مسجلة",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFEDE1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large "إضافة عينة" button
                    Button(
                        onClick = { showAddSampleDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEDE1),
                            contentColor = Color(0xFFEA580C)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_add_sample_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إضافة عينة جديدة",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable list of categorized samples
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        item {
                            Text(
                                text = "العينات المضافة",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Viruses
                        if (virusSamples.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الفيروسات",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = Color(0xFFE11D48)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Coronavirus,
                                        contentDescription = null,
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(virusSamples, key = { it.id }) { sample ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    SamplePillItem(
                                        sample = sample,
                                        onDelete = {
                                            viewModel.deleteSample(sample.id)
                                            Toast.makeText(context, "تم حذف العينة", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                            item { Spacer(modifier = Modifier.height(14.dp)) }
                        }

                        // Pests
                        if (pestSamples.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الآفات",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = Color(0xFFEA580C)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = Color(0xFFEA580C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(pestSamples, key = { it.id }) { sample ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    SamplePillItem(
                                        sample = sample,
                                        onDelete = {
                                            viewModel.deleteSample(sample.id)
                                            Toast.makeText(context, "تم حذف العينة", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                            item { Spacer(modifier = Modifier.height(14.dp)) }
                        }

                        // Beneficials
                        if (beneficialSamples.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "حشرات نافعة",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        ),
                                        color = Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(beneficialSamples, key = { it.id }) { sample ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    SamplePillItem(
                                        sample = sample,
                                        onDelete = {
                                            viewModel.deleteSample(sample.id)
                                            Toast.makeText(context, "تم حذف العينة", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }

                        if (registeredSamples.isEmpty()) {
                            item {
                                Text(
                                    text = "لا توجد عينات مسجلة حالياً.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Close Button
                    Button(
                        onClick = { showSamplesDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تم", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // 2. Add Sample Dialog (matching Screenshot 2)
    if (showAddSampleDialog) {
        var newArabicName by remember { mutableStateOf("") }
        var newForeignName by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf(SampleCategory.VIRUS) }

        Dialog(onDismissRequest = { showAddSampleDialog = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header Row: Close on left, Title & Icon on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close icon in gray circle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .clickable { showAddSampleDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Title and Orange Plus Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "إضافة عينة",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEDE1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Field 1: اسم العينة بالعربي
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسم العينة بالعربي",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newArabicName,
                        onValueChange = { newArabicName = it },
                        placeholder = {
                            Text(
                                text = "ا ب ت",
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = Color(0xFFE2E8F0),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: اسم العينة بالأجنبي
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسم العينة بالأجنبي",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newForeignName,
                        onValueChange = { newForeignName = it },
                        placeholder = {
                            Text(
                                text = "Abc",
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = Color(0xFFE2E8F0),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 3: نوع العينة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نوع العينة",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category options (فيروس, آفة, حشرة نافعة)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf(
                            SampleCategory.BENEFICIAL to "حشرة نافعة",
                            SampleCategory.PEST to "آفة",
                            SampleCategory.VIRUS to "فيروس"
                        )

                        categories.forEach { (cat, label) ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedCategory = cat },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFFFFEDE1) else Color(0xFFF1F5F9),
                                border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFEA580C)) else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        ),
                                        color = if (isSelected) Color(0xFFEA580C) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons: إلغاء & إضافة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showAddSampleDialog = false },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "إلغاء",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (newArabicName.isBlank()) {
                                        Toast.makeText(context, "يرجى إدخال اسم العينة بالعربي", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.addSample(newArabicName, newForeignName, selectedCategory)
                                        Toast.makeText(context, "تمت إضافة العينة بنجاح", Toast.LENGTH_SHORT).show()
                                        showAddSampleDialog = false
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEA580C)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "إضافة",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Export Dialog
    if (showExportDialog) {
        val languages = listOf("الفرنسية", "العربية", "الإنجليزية")
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("إعدادات التصدير", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("اختر لغة تقارير التصدير:", style = MaterialTheme.typography.bodyMedium)
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    currentExportLang = lang
                                    viewModel.prefs.exportLanguage = lang
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentExportLang == lang) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                            } else {
                                Spacer(modifier = Modifier.size(24.dp))
                            }
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currentExportLang == lang) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Button(
                        onClick = {
                            viewModel.shareAllReportsSummary(context, allReports)
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة وتصدير ملخص التقارير الآن")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("تم")
                }
            }
        )
    }

    // 4. Theme Dialog
    if (showThemeDialog) {
        val themeModes = listOf("تلقائي", "فاتح", "داكن")
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("المظهر", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    themeModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentThemeMode == mode) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                            } else {
                                Spacer(modifier = Modifier.size(24.dp))
                            }
                            Text(
                                text = mode,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currentThemeMode == mode) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("تم")
                }
            }
        )
    }

    // 5. More / About Dialog
    if (showMoreDialog) {
        AlertDialog(
            onDismissRequest = { showMoreDialog = false },
            title = {
                Text("معلومات التطبيق والمطور", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "اسم التطبيق: ${AppPreferences.APP_TITLE}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "المطور: ${AppPreferences.DEVELOPER_NAME}",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "الإصدار: ${AppPreferences.APP_VERSION}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تطبيق زراعي ذكي وشامل لمراقبة أمراض النباتات، الفيروسات، الآفات والحشرات النافعة مع تتبع الحقول والقطاعات وتوثيق التقارير الميدانية بدقة عالية.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showMoreDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // 6. Trash Dialog
    if (showTrashDialog) {
        AlertDialog(
            onDismissRequest = { showTrashDialog = false },
            title = { Text("سلة المهملات (${trashReports.size})", fontWeight = FontWeight.Bold) },
            text = {
                if (trashReports.isEmpty()) {
                    Text(
                        "سلة المهملات فارغة حالياً.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trashReports) { report ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GrayButtonBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        TextButton(onClick = { viewModel.restoreReport(report) }) {
                                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("استعادة", fontSize = 12.sp)
                                        }
                                        TextButton(onClick = { viewModel.deletePermanently(report) }) {
                                            Text("حذف نهائي", color = AccentVirusRed, fontSize = 12.sp)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("حقل ${report.fieldNumber} - قطاع ${report.sector}", fontWeight = FontWeight.Bold)
                                        Text(report.observationDate, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (trashReports.isNotEmpty()) {
                    Button(
                        onClick = {
                            viewModel.emptyTrash()
                            Toast.makeText(context, "تم إفراغ سلة المهملات", Toast.LENGTH_SHORT).show()
                            showTrashDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentVirusRed)
                    ) {
                        Text("تفريغ الكل")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showTrashDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // 7. Factory Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة ضبط المصنع", fontWeight = FontWeight.Bold, color = AccentVirusRed) },
            text = {
                Text("هل أنت متأكد من إعادة ضبط المصنع؟ سيتم مسح جميع التقارير وإعادة تهيئة التطبيق بالعينات والتقارير النموذجية الأولية.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                        Toast.makeText(context, "تمت إعادة التهيئة بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentVirusRed)
                ) {
                    Text("تأكيد التهيئة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 8. Auto-Refresh Dialog
    if (showAutoRefreshDialog) {
        AlertDialog(
            onDismissRequest = { showAutoRefreshDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text("التحديث التلقائي", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "يعمل التحديث التلقائي على مزامنة وتحديث شاشات وقوائم التقارير والعينات دورياً بشكل أوتوماتيكي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isAutoRefreshEnabled,
                            onCheckedChange = { viewModel.setAutoRefreshEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldPrimary
                            )
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "تفعيل التحديث التلقائي",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isAutoRefreshEnabled) "يعمل بالخلفية دورياً" else "متوقف حالياً",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (isAutoRefreshEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "معدل التحديث الزمني:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val intervals = listOf(
                            5 to "كل 5 ثوانٍ",
                            10 to "كل 10 ثوانٍ",
                            30 to "كل 30 ثانية",
                            60 to "كل دقيقة"
                        )

                        intervals.forEach { (sec, label) ->
                            val isSelected = autoRefreshInterval == sec
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { viewModel.setAutoRefreshInterval(sec) }
                                    .testTag("interval_option_$sec")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(18.dp))
                                    }
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Manual immediate refresh button with confirmation
                    Button(
                        onClick = {
                            showManualRefreshConfirmDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_refresh_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إجراء تحديث يدوي فوري", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAutoRefreshDialog = false }) {
                    Text("تم")
                }
            }
        )
    }

    // 8b. Manual Refresh Confirmation Dialog
    if (showManualRefreshConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showManualRefreshConfirmDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تأكيد التحديث اليدوي", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            text = {
                Text(
                    text = "هل ترغب في إجراء تحديث يدوي فوري ومزامنة كافة السجلات والبيانات الآن؟",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManualRefreshConfirmDialog = false
                        viewModel.triggerRefresh()
                        Toast.makeText(context, "تم التحديث اليدوي الفوري ومزامنة البيانات بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_manual_refresh_button")
                ) {
                    Text("تأكيد التحديث", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showManualRefreshConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_manual_refresh_button")
                ) {
                    Text("إلغاء")
                }
            }
        )
    }

    // 9. What's New Dialog (ما الجديد في هاته النسخة)
    if (showWhatsNewDialog) {
        Dialog(onDismissRequest = { showWhatsNewDialog = false }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Icon
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NewReleases,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ما الجديد في هاته النسخة",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "الإصدار v${AppPreferences.APP_VERSION} • 11-09-2026",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(14.dp))

                    // Features list
                    val updates = listOf(
                        "💾 ميزة النسخ الاحتياطي والاستعادة" to "إنشاء وتصدير واسترجاع كامل بيانات التقارير والعينات والإعدادات بأمان، مع دعم الحفظ في الهاتف والمشاركة وخيارات الدمج أو الاستبدال الشامل.",
                        "🧪 إدارة العينات المتكاملة" to "إمكانية استعراض وإضافة وحذف عينات الفيروسات والآفات والحشرات النافعة مباشرة من الإعدادات.",
                        "🌙 الوضع الداكن المحسن" to "تطبيق فوري للوضع الداكن المريح للعين مع حفظ تفضيلات المظهر.",
                        "🔄 التحديث التلقائي الذكي" to "مزامنة البيانات والحقول أوتوماتيكياً كل بضع ثوانٍ.",
                        "📄 تصدير متقدم للتقارير" to "مشاركة وتصدير التقارير بصيغ متعددة (PDF، صور، ونصوص) بدقة فائقة.",
                        "🗑️ سلة مهملات سريعة" to "إمكانية استرجاع التقارير المحذوفة أو تفريغها بنقرة واحدة."
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        updates.forEach { (title, desc) ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showWhatsNewDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("رائع، فهمت", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // 10. App Update Dialog (تحديث التطبيق - سحابي OTA و محلي APK)
    if (showUpdateDialog) {
        val coroutineScope = rememberCoroutineScope()
        var selectedUpdateTab by remember { mutableStateOf(0) } // 0 = OTA, 1 = Local APK

        // OTA State
        var isCheckingOnline by remember { mutableStateOf(false) }
        var onlineCheckDone by remember { mutableStateOf(false) }
        var onlineCheckError by remember { mutableStateOf<String?>(null) }
        var availableUpdateInfo by remember { mutableStateOf<UpdateReleaseInfo?>(null) }
        var isDownloadingApk by remember { mutableStateOf(false) }
        var downloadProgress by remember { mutableStateOf(0f) }
        var downloadError by remember { mutableStateOf<String?>(null) }

        // Local APK State
        var isInspectingLocalApk by remember { mutableStateOf(false) }
        var localApkInfo by remember { mutableStateOf<LocalApkInfo?>(null) }
        var localApkError by remember { mutableStateOf<String?>(null) }

        val apkFilePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                isInspectingLocalApk = true
                localApkError = null
                localApkInfo = null
                coroutineScope.launch {
                    val result = AppUpdateManager.inspectLocalApk(context, uri)
                    isInspectingLocalApk = false
                    result.onSuccess { info ->
                        localApkInfo = info
                    }.onFailure { err ->
                        localApkError = err.localizedMessage ?: "الملف المحدد غير صالح أو تالف"
                    }
                }
            }
        }

        Dialog(onDismissRequest = {
            if (!isDownloadingApk) {
                showUpdateDialog = false
            }
        }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Icon & Title
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0284C7).copy(alpha = 0.12f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "مركز تحديث التطبيق",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "الإصدار الحالي: v${AppPreferences.APP_VERSION} • 11-09-2026",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector
                    TabRow(
                        selectedTabIndex = selectedUpdateTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        contentColor = Color(0xFF0284C7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedUpdateTab == 0,
                            onClick = { selectedUpdateTab = 0 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "تحديث سحابي (OTA)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedUpdateTab == 1,
                            onClick = { selectedUpdateTab = 1 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "رفع ملف APK",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // TAB 0: OTA Cloud Update
                    if (selectedUpdateTab == 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isCheckingOnline) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(30.dp),
                                            color = Color(0xFF0284C7),
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "جاري الاتصال بالسيرفر والتحقق من الإصدارات...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            } else if (availableUpdateInfo != null && availableUpdateInfo!!.isNewer) {
                                val info = availableUpdateInfo!!
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.08f),
                                    border = BorderStroke(1.5.dp, EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = EmeraldPrimary,
                                                modifier = Modifier.padding(2.dp)
                                            ) {
                                                Text(
                                                    text = "نسخة جديدة متاحة",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Text(
                                                text = "الإصدار v${info.versionName}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldPrimary
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "تاريخ الإصدار: ${info.releaseDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = EmeraldPrimary.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "ما الجديد في هذا الإصدار:",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        info.changelog.forEach { note ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    text = note,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                    color = TextSecondary,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("•", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (isDownloadingApk) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                LinearProgressIndicator(
                                                    progress = { downloadProgress },
                                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                                    color = EmeraldPrimary,
                                                    trackColor = EmeraldPrimary.copy(alpha = 0.2f)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "جاري تحميل التحديث: ${(downloadProgress * 100).toInt()}%",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    isDownloadingApk = true
                                                    downloadProgress = 0f
                                                    downloadError = null
                                                    coroutineScope.launch {
                                                        val destFile = File(context.cacheDir, "sf_surveillance_update.apk")
                                                        if (info.downloadUrl.isNotBlank() && info.downloadUrl.startsWith("http")) {
                                                            val result = AppUpdateManager.downloadApk(info.downloadUrl, destFile) { p ->
                                                                downloadProgress = p
                                                            }
                                                            isDownloadingApk = false
                                                            result.onSuccess { file ->
                                                                val installRes = AppUpdateManager.installApk(context, file)
                                                                installRes.onFailure { err ->
                                                                    Toast.makeText(context, err.localizedMessage ?: "حدث خطأ أثناء تشغيل المثبت", Toast.LENGTH_LONG).show()
                                                                }
                                                            }.onFailure { err ->
                                                                downloadError = err.localizedMessage ?: "تعذر تحميل ملف التحديث"
                                                            }
                                                        } else {
                                                            // Simulated fast download demonstration
                                                            for (step in 1..10) {
                                                                delay(120)
                                                                downloadProgress = step / 10f
                                                            }
                                                            isDownloadingApk = false
                                                            // Try to create mock apk or install
                                                            destFile.writeText("sample_update")
                                                            Toast.makeText(context, "اكتمل التحميل! جاري تشغيل مثبت الحزم...", Toast.LENGTH_SHORT).show()
                                                            AppUpdateManager.installApk(context, destFile)
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth().testTag("download_and_install_button")
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("تحميل وتثبيت التحديث الآن", fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }

                                        if (downloadError != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = downloadError!!,
                                                color = Color(0xFFEF4444),
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            } else if (onlineCheckDone) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "تطبيقك محدث إلى أحدث إصدار متاح (v${AppPreferences.APP_VERSION})!",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "النسخة الحالية مستقرة ولا توجد تحديثات جديدة على الخادم.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else if (onlineCheckError != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "تعذر الاتصال بالخادم السحابي",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFDC2626)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "يرجى التحقق من توفر اتصال بالإنترنت، أو استخدام تبويب (رفع ملف APK) للتثبيت اليدوي فوراً دون إنترنت.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.End
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Demo preview option
                                        TextButton(
                                            onClick = {
                                                onlineCheckError = null
                                                onlineCheckDone = true
                                                availableUpdateInfo = UpdateReleaseInfo(
                                                    versionName = "1.6",
                                                    versionCode = 16,
                                                    releaseDate = "12-09-2026",
                                                    downloadUrl = "",
                                                    changelog = listOf(
                                                        "تحسين سرعة معالجة وتصدير التقارير الميدانية",
                                                        "إضافة مؤشرات حيوية وتفاعلية جديدة للعينات والآفات",
                                                        "تحسين التحديث التلقائي والمزامنة بالخلفية"
                                                    ),
                                                    isNewer = true
                                                )
                                            },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text("معاينة تجربة التحديث السحابي (v1.6 تجريبي)", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "التحديث الذكي عبر الإنترنت (OTA)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "يتصل التطبيق بالخادم لمقارنة النسخة وتنزيل أحدث التحديثات والميزات تلقائياً بنقرة واحدة.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action button: Check Online
                            Button(
                                onClick = {
                                    if (!isCheckingOnline) {
                                        isCheckingOnline = true
                                        onlineCheckDone = false
                                        onlineCheckError = null
                                        availableUpdateInfo = null
                                        coroutineScope.launch {
                                            val result = AppUpdateManager.checkOnlineUpdate(viewModel.prefs.updateServerUrl)
                                            isCheckingOnline = false
                                            result.onSuccess { info ->
                                                onlineCheckDone = true
                                                if (info != null && info.isNewer) {
                                                    availableUpdateInfo = info
                                                } else {
                                                    availableUpdateInfo = null
                                                }
                                            }.onFailure { err ->
                                                onlineCheckError = err.localizedMessage ?: "تعذر الاتصال بالخادم"
                                            }
                                        }
                                    }
                                },
                                enabled = !isCheckingOnline && !isDownloadingApk,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("check_online_updates_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (onlineCheckDone) "إعادة الفحص عبر الإنترنت" else "فحص التحديثات الآن",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // TAB 1: Local APK Installer
                    if (selectedUpdateTab == 1) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "تثبيت تحديث APK من الهاتف",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = AccentTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "اختر ملف تحديث (.apk) تم إرساله إليك عبر الواتساب أو التليجرام أو البريد لتثبيته مباشرة دون إنترنت.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isInspectingLocalApk) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = AccentTeal,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "جاري فحص والتحقق من حزمة الـ APK...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (localApkInfo != null) {
                                val apk = localApkInfo!!
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.08f),
                                    border = BorderStroke(1.5.dp, EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Text(
                                                text = "تم فحص ملف التحديث بنجاح",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldPrimary
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = EmeraldPrimary.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(apk.appLabel, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                                Text("اسم التطبيق:", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("v${apk.versionName} (${apk.versionCode})", fontWeight = FontWeight.Bold, color = EmeraldPrimary, style = MaterialTheme.typography.bodySmall)
                                                Text("الإصدار المكتشف:", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("${apk.fileSizeMb} MB", style = MaterialTheme.typography.bodySmall)
                                                Text("حجم الملف:", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Button(
                                            onClick = {
                                                val installRes = AppUpdateManager.installApk(context, apk.file)
                                                installRes.onSuccess {
                                                    Toast.makeText(context, "جاري فتح أداة تثبيت التحديث...", Toast.LENGTH_SHORT).show()
                                                }.onFailure { err ->
                                                    Toast.makeText(context, err.localizedMessage ?: "تعذر بدء التثبيت", Toast.LENGTH_LONG).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("install_local_apk_button")
                                        ) {
                                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تثبيت هذا التحديث الآن", fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }

                            if (localApkError != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "خطأ في قراءة ملف التحديث",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFDC2626)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = localApkError!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // File picker trigger button
                            Button(
                                onClick = {
                                    try {
                                        apkFilePickerLauncher.launch("*/*")
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "تعذر فتح منتقي الملفات: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("pick_apk_file_button")
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (localApkInfo != null) "اختيار ملف APK آخر" else "رفع واختيار ملف تحديث (APK) من الهاتف",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { showUpdateDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق", color = TextSecondary)
                    }
                }
            }
        }
    }

    // 10. نافذة النسخ الاحتياطي والاستعادة (Backup & Restore Dialog)
    if (showBackupDialog) {
        val coroutineScope = rememberCoroutineScope()
        var selectedBackupTab by remember { mutableStateOf(0) }
        var isBackingUp by remember { mutableStateOf(false) }
        var isRestoring by remember { mutableStateOf(false) }
        var backupActionStatusMessage by remember { mutableStateOf<String?>(null) }
        var backupActionErrorMessage by remember { mutableStateOf<String?>(null) }

        // Restore tab state
        var inspectedMetadata by remember { mutableStateOf<BackupMetadata?>(null) }
        var inspectedSourceTitle by remember { mutableStateOf<String?>(null) }
        var restoreError by remember { mutableStateOf<String?>(null) }
        var restoreSuccessMessage by remember { mutableStateOf<String?>(null) }
        var restoreMode by remember { mutableStateOf(0) } // 0 = Merge (دمج), 1 = Overwrite (استبدال)

        // Local backups state
        var localBackupsList by remember { mutableStateOf(viewModel.getLocalBackups(context)) }

        val refreshLocalBackups: () -> Unit = {
            localBackupsList = viewModel.getLocalBackups(context)
        }

        // Launcher for saving backup file directly to phone storage
        val saveDocumentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    isBackingUp = true
                    backupActionStatusMessage = null
                    backupActionErrorMessage = null
                    try {
                        val payload = viewModel.createBackupPayload()
                        val result = BackupManager.writeBackupToUri(context, uri, payload)
                        if (result.isSuccess) {
                            backupActionStatusMessage = "تم حفظ ملف النسخة الاحتياطية بنجاح على هاتفك!"
                            Toast.makeText(context, "تم حفظ النسخة الاحتياطية بنجاح!", Toast.LENGTH_SHORT).show()
                            refreshLocalBackups()
                        } else {
                            backupActionErrorMessage = "تعذر كتابة الملف: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        backupActionErrorMessage = "خطأ أثناء الحفظ: ${e.message}"
                    } finally {
                        isBackingUp = false
                    }
                }
            }
        }

        // Launcher for picking backup file from device
        val pickBackupFileLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    restoreError = null
                    restoreSuccessMessage = null
                    val result = BackupManager.inspectBackupUri(context, uri)
                    if (result.isSuccess) {
                        inspectedMetadata = result.getOrNull()
                        inspectedSourceTitle = "ملف خارجي من الهاتف"
                    } else {
                        inspectedMetadata = null
                        restoreError = result.exceptionOrNull()?.message ?: "الملف المحدد غير صالح كنسخة احتياطية"
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .testTag("backup_restore_dialog"),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            confirmButton = {},
            dismissButton = {},
            text = {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header icon badge
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = null,
                                tint = Color(0xFF0D9488),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "النسخ الاحتياطي والاستعادة",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "حفظ واسترجاع كافة بيانات التقارير والعينات المسجلة بأمان",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab selector
                        TabRow(
                            selectedTabIndex = selectedBackupTab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            contentColor = Color(0xFF0D9488),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Tab(
                                selected = selectedBackupTab == 0,
                                onClick = { selectedBackupTab = 0 },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text("إنشاء وتصدير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                            Tab(
                                selected = selectedBackupTab == 1,
                                onClick = { selectedBackupTab = 1 },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text("استعادة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                            Tab(
                                selected = selectedBackupTab == 2,
                                onClick = {
                                    selectedBackupTab = 2
                                    refreshLocalBackups()
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Text("المحلية (${localBackupsList.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab Contents
                        when (selectedBackupTab) {
                            0 -> {
                                // Tab 0: إنشاء وتصدير
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Current data summary card
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFF0D9488).copy(alpha = 0.08f),
                                        border = BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "بيانات التطبيق الحالية",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFF0D9488)
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF0D9488).copy(alpha = 0.18f)
                                                ) {
                                                    Text(
                                                        text = "v${AppPreferences.APP_VERSION}",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        ),
                                                        color = Color(0xFF0D9488),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            HorizontalDivider(color = Color(0xFF0D9488).copy(alpha = 0.15f))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("إجمالي التقارير", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text(
                                                        text = "${allReports.size + trashCount} تقرير (${allReports.size} نشط)",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("العينات المسجلة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text(
                                                        text = "${registeredSamples.size} عينة",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("المراقب المعتمد", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                Text(
                                                    text = viewModel.prefs.defaultObserverName.ifBlank { "غير محدد" },
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    // Action status banner
                                    if (backupActionStatusMessage != null) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFECFDF5),
                                            border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                                Text(
                                                    text = backupActionStatusMessage ?: "",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = EmeraldPrimary
                                                )
                                            }
                                        }
                                    }

                                    if (backupActionErrorMessage != null) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFFEF2F2),
                                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                                Text(
                                                    text = backupActionErrorMessage ?: "",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }

                                    if (isBackingUp) {
                                        LinearProgressIndicator(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFF0D9488)
                                        )
                                    }

                                    // Primary Action: Save to Device
                                    Button(
                                        onClick = {
                                            val defaultName = BackupManager.generateDefaultFileName()
                                            try {
                                                saveDocumentLauncher.launch(defaultName)
                                            } catch (e: Exception) {
                                                backupActionErrorMessage = "تعذر فتح حافظة الملفات: ${e.message}"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("save_backup_device_button")
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("حفظ نسخة احتياطية على الهاتف (.json)", fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    // Secondary Action: Share via Apps
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBackingUp = true
                                                backupActionStatusMessage = null
                                                backupActionErrorMessage = null
                                                try {
                                                    val payload = viewModel.createBackupPayload()
                                                    val res = BackupManager.shareBackup(context, payload)
                                                    if (res.isFailure) {
                                                        backupActionErrorMessage = "تعذر المشاركة: ${res.exceptionOrNull()?.message}"
                                                    }
                                                } catch (e: Exception) {
                                                    backupActionErrorMessage = "خطأ أثناء تجهيز النسخة: ${e.message}"
                                                } finally {
                                                    isBackingUp = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("share_backup_button")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("مشاركة النسخة (واتساب، بريد، درايف)", fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    // Quick Local Backup
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBackingUp = true
                                                backupActionStatusMessage = null
                                                backupActionErrorMessage = null
                                                val res = viewModel.saveLocalBackup(context)
                                                isBackingUp = false
                                                if (res.isSuccess) {
                                                    backupActionStatusMessage = "تم حفظ نسخة احتياطية محلية سريعة بنجاح!"
                                                    Toast.makeText(context, "تم حفظ النسخة المحلية!", Toast.LENGTH_SHORT).show()
                                                    refreshLocalBackups()
                                                } else {
                                                    backupActionErrorMessage = "تعذر الحفظ المحلي: ${res.exceptionOrNull()?.message}"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("quick_local_backup_button")
                                    ) {
                                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("أخذ نسخة احتياطية محلية سريعة بنقرة واحدة", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            1 -> {
                                // Tab 1: استعادة نسخة
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Choose File Button
                                    Button(
                                        onClick = {
                                            try {
                                                pickBackupFileLauncher.launch("*/*")
                                            } catch (e: Exception) {
                                                restoreError = "تعذر فتح منتقي الملفات: ${e.message}"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("pick_backup_file_button")
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (inspectedMetadata != null) "اختيار ملف نسخة احتياطية آخر" else "اختيار ملف نسخة احتياطية (.json) من الهاتف",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Restore Error
                                    if (restoreError != null) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFFEF2F2),
                                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                                Text(
                                                    text = restoreError ?: "",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }

                                    // Restore Success
                                    if (restoreSuccessMessage != null) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFECFDF5),
                                            border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(32.dp))
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = restoreSuccessMessage ?: "",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }

                                    // Inspected Metadata Card
                                    val meta = inspectedMetadata
                                    if (meta != null) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFFF0FDF4),
                                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                                        Text(
                                                            text = "بيانات النسخة المكتشفة",
                                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = EmeraldPrimary
                                                        )
                                                    }
                                                    if (inspectedSourceTitle != null) {
                                                        Text(
                                                            text = inspectedSourceTitle ?: "",
                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                            color = TextSecondary
                                                        )
                                                    }
                                                }

                                                HorizontalDivider(color = EmeraldPrimary.copy(alpha = 0.2f))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("تاريخ النسخة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text(meta.createdAtFormatted, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("إصدار النسخة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text("v${meta.appVersion}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("التقارير المضمنة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text(
                                                        "${meta.totalReportsCount} تقرير (${meta.activeReportsCount} نشط، ${meta.trashReportsCount} سلة)",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = EmeraldPrimary
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("العينات المسجلة", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                    Text("${meta.registeredSamplesCount} عينة", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                }

                                                if (meta.observerName.isNotBlank()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("اسم المراقب", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                        Text(meta.observerName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                                    }
                                                }

                                                if (meta.fileSizeFormatted.isNotBlank()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("حجم الملف", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                        Text(meta.fileSizeFormatted, style = MaterialTheme.typography.bodySmall)
                                                    }
                                                }
                                            }
                                        }

                                        // Restore Mode Options
                                        Text(
                                            text = "طريقة استعادة البيانات:",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        // Option 0: Merge
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (restoreMode == 0) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(
                                                width = if (restoreMode == 0) 2.dp else 1.dp,
                                                color = if (restoreMode == 0) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { restoreMode = 0 }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(if (restoreMode == 0) EmeraldPrimary else Color.Transparent)
                                                        .border(2.dp, if (restoreMode == 0) EmeraldPrimary else MaterialTheme.colorScheme.outline, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (restoreMode == 0) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                Column {
                                                    Text(
                                                        text = "دمج مع البيانات الحالية (موصى به)",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "إضافة التقارير والعينات من النسخة مع الاحتفاظ التام بتقاريرك الحالية دون حذف.",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                        }

                                        // Option 1: Full Overwrite
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (restoreMode == 1) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(
                                                width = if (restoreMode == 1) 2.dp else 1.dp,
                                                color = if (restoreMode == 1) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { restoreMode = 1 }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(if (restoreMode == 1) Color(0xFFEF4444) else Color.Transparent)
                                                        .border(2.dp, if (restoreMode == 1) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (restoreMode == 1) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                Column {
                                                    Text(
                                                        text = "استبدال كامل وشامل (مسح البيانات الحالية)",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = if (restoreMode == 1) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "مسح جميع التقارير الحالية واستبدالها بالكامل ببيانات هذه النسخة الاحتياطية.",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                        }

                                        if (isRestoring) {
                                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = EmeraldPrimary)
                                        }

                                        // Execute Restore Button
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    isRestoring = true
                                                    restoreError = null
                                                    restoreSuccessMessage = null
                                                    val res = viewModel.restoreBackup(meta, replaceAll = (restoreMode == 1))
                                                    isRestoring = false
                                                    if (res.isSuccess) {
                                                        val count = res.getOrNull() ?: 0
                                                        restoreSuccessMessage = "تمت استعادة $count تقرير بنجاح وتحديث السجلات!"
                                                        Toast.makeText(context, "تمت الاستعادة بنجاح!", Toast.LENGTH_SHORT).show()
                                                        inspectedMetadata = null
                                                    } else {
                                                        restoreError = "حدث خطأ أثناء الاستعادة: ${res.exceptionOrNull()?.message}"
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (restoreMode == 1) Color(0xFFDC2626) else EmeraldPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("confirm_restore_button")
                                        ) {
                                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (restoreMode == 1) "تأكيد الاستبدال الشامل واستعادة النسخة" else "بدء استعادة ودمج البيانات الآن",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    } else {
                                        // Helper info card when no file selected
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CloudDownload,
                                                    contentDescription = null,
                                                    tint = Color(0xFF0D9488),
                                                    modifier = Modifier.size(36.dp)
                                                )
                                                Text(
                                                    text = "استرجاع التقارير والعينات المسجلة",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = "قم باختيار ملف نسخة احتياطية (.json) محفوظ على جهازك أو تم استلامه من زميل لاستيراد ومزامنة البيانات مع تطبيقك.",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                    color = TextSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // Tab 2: النسخ المحلية
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (localBackupsList.isEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Folder,
                                                    contentDescription = null,
                                                    tint = TextTertiary,
                                                    modifier = Modifier.size(42.dp)
                                                )
                                                Text(
                                                    text = "لا توجد نسخ احتياطية محلية محفوظة",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "يمكنك إنشاء نسخة محلية سريعة بضغطة زر لحفظ بياناتك في الذاكرة الآمنة للتطبيق.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            isBackingUp = true
                                                            viewModel.saveLocalBackup(context)
                                                            isBackingUp = false
                                                            refreshLocalBackups()
                                                            Toast.makeText(context, "تم أخذ نسخة محلية بنجاح!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("إنشاء نسخة محلية الآن", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "النسخ المحفوظة على ذاكرة التطبيق (${localBackupsList.size}):",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        localBackupsList.forEach { backupItem ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = MaterialTheme.colorScheme.surface,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(Color(0xFF0D9488).copy(alpha = 0.12f)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(20.dp))
                                                            }
                                                            Column {
                                                                Text(
                                                                    text = backupItem.formattedDate,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Text(
                                                                    text = "${backupItem.reportsCount} تقرير  •  ${backupItem.samplesCount} عينات  •  ${backupItem.sizeText}",
                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                                    color = TextSecondary
                                                                )
                                                            }
                                                        }
                                                    }

                                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                                                    // Action buttons row
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Delete button
                                                        TextButton(
                                                            onClick = {
                                                                viewModel.deleteLocalBackup(backupItem.file)
                                                                refreshLocalBackups()
                                                                Toast.makeText(context, "تم حذف النسخة", Toast.LENGTH_SHORT).show()
                                                            },
                                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                                                        ) {
                                                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("حذف", fontSize = 12.sp)
                                                        }

                                                        Spacer(modifier = Modifier.width(6.dp))

                                                        // Share button
                                                        TextButton(
                                                            onClick = {
                                                                val json = backupItem.file.readText()
                                                                BackupManager.shareBackup(context, json)
                                                            },
                                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0284C7))
                                                        ) {
                                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("مشاركة", fontSize = 12.sp)
                                                        }

                                                        Spacer(modifier = Modifier.width(6.dp))

                                                        // Restore button
                                                        Button(
                                                            onClick = {
                                                                val inspected = BackupManager.inspectBackupFile(backupItem.file)
                                                                if (inspected.isSuccess) {
                                                                    inspectedMetadata = inspected.getOrNull()
                                                                    inspectedSourceTitle = backupItem.fileName
                                                                    selectedBackupTab = 1
                                                                } else {
                                                                    Toast.makeText(context, "الملف غير صالح", Toast.LENGTH_SHORT).show()
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                                            shape = RoundedCornerShape(8.dp),
                                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                        ) {
                                                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("استعادة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        TextButton(
                            onClick = { showBackupDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("close_backup_dialog_button")
                        ) {
                            Text("إغلاق", color = TextSecondary)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SettingsCardItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Chevron icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(24.dp)
            )

            // Right: Text + Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SamplePillItem(
    sample: SampleItem,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Color(0xFFFED7AA)),
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete 'x' icon button (placed on the right in RTL)
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف العينة",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sample name text
            val text = if (sample.nameScientific.isNotBlank() && sample.nameScientific != sample.nameArabic) {
                "${sample.nameArabic} (${sample.nameScientific})"
            } else {
                sample.nameArabic
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ExportSettingsCard(
    viewModel: ReportViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var currentLang by remember { mutableStateOf(viewModel.prefs.exportLanguage) }
    var currentQuality by remember { mutableStateOf(viewModel.prefs.exportQuality) }

    var includeViruses by remember { mutableStateOf(viewModel.prefs.exportIncludeViruses) }
    var includePests by remember { mutableStateOf(viewModel.prefs.exportIncludePests) }
    var includeBeneficials by remember { mutableStateOf(viewModel.prefs.exportIncludeBeneficials) }
    var includeNotes by remember { mutableStateOf(viewModel.prefs.exportIncludeSpecialNotes) }

    var isLangExpanded by remember { mutableStateOf(false) }
    var isElementsExpanded by remember { mutableStateOf(false) }
    var isQualityExpanded by remember { mutableStateOf(false) }

    val activeElementsCount = listOf(includeViruses, includePests, includeBeneficials, includeNotes).count { it }
    val totalElements = 4

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_card_export_interactive")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            // Main Card Header (Clickable to expand / collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Chevron
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowLeft,
                    contentDescription = if (isExpanded) "طي" else "توسيع",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )

                // Right: Text + Purple Circular Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "التصدير",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "اللغة: $currentLang",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "التصدير",
                            tint = Color(0xFF9333EA),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Expanded Sub-Cards (Matching user's exact design)
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sub-Card 1: لغة التقرير
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isLangExpanded = !isLangExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = if (isLangExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(22.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "لغة التقرير",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = currentLang,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.5.sp
                                            ),
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Translate,
                                            contentDescription = "لغة التقرير",
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Language Options Selector
                            AnimatedVisibility(visible = isLangExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    listOf(
                                        "الفرنسية" to "Français (Langue officielle des rapports)",
                                        "العربية" to "العربية (التقارير باللغة العربية)",
                                        "الإنجليزية" to "English (International format)"
                                    ).forEach { (lang, desc) ->
                                        val isSelected = currentLang == lang
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    currentLang = lang
                                                    viewModel.prefs.exportLanguage = lang
                                                    Toast.makeText(context, "تم تحديد لغة التقرير: $lang", Toast.LENGTH_SHORT).show()
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(0xFF0284C7),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier.size(18.dp))
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = lang,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            fontSize = 13.5.sp
                                                        ),
                                                        color = if (isSelected) Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = desc,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sub-Card 2: عناصر التقرير
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isElementsExpanded = !isElementsExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = if (isElementsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(22.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "عناصر التقرير",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "$activeElementsCount مفعلة من $totalElements",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.5.sp
                                            ),
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDCFCE7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatListBulleted,
                                            contentDescription = "عناصر التقرير",
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Elements Switches
                            AnimatedVisibility(visible = isElementsExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))

                                    // Element 1: الفيروسات
                                    ReportElementToggleRow(
                                        title = "1. الفيروسات (Virus)",
                                        isChecked = includeViruses,
                                        onCheckedChange = {
                                            includeViruses = it
                                            viewModel.prefs.exportIncludeViruses = it
                                        }
                                    )

                                    // Element 2: الآفات والأمراض
                                    ReportElementToggleRow(
                                        title = "2. الآفات والأمراض (Ravageurs & Maladies)",
                                        isChecked = includePests,
                                        onCheckedChange = {
                                            includePests = it
                                            viewModel.prefs.exportIncludePests = it
                                        }
                                    )

                                    // Element 3: الحشرات النافعة
                                    ReportElementToggleRow(
                                        title = "3. الحشرات النافعة (Auxiliaires)",
                                        isChecked = includeBeneficials,
                                        onCheckedChange = {
                                            includeBeneficials = it
                                            viewModel.prefs.exportIncludeBeneficials = it
                                        }
                                    )

                                    // Element 4: الملاحظات الخاصة
                                    ReportElementToggleRow(
                                        title = "4. الملاحظات الخاصة (Remarques Spéciales)",
                                        isChecked = includeNotes,
                                        onCheckedChange = {
                                            includeNotes = it
                                            viewModel.prefs.exportIncludeSpecialNotes = it
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Sub-Card 3: جودة ملف التقرير
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isQualityExpanded = !isQualityExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = if (isQualityExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(22.dp)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "جودة ملف التقرير",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "الصورة > $currentQuality",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.5.sp
                                            ),
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF3E8FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HighQuality,
                                            contentDescription = "جودة ملف التقرير",
                                            tint = Color(0xFF9333EA),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Quality Options Selector
                            AnimatedVisibility(visible = isQualityExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    listOf(
                                        "عالية" to "دقة فائقة (300 DPI) مناسبة للطباعة والأرشفة",
                                        "متوسطة" to "دقة متوازنة (150 DPI) سريعة ومثالية للمشاركة",
                                        "اقتصادية" to "حجم ملف مضغوط (72 DPI) خفيف لواتساب"
                                    ).forEach { (q, desc) ->
                                        val isSelected = currentQuality == q
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) Color(0xFFFAF5FF) else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF9333EA) else Color(0xFFE2E8F0)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    currentQuality = q
                                                    viewModel.prefs.exportQuality = q
                                                    Toast.makeText(context, "تم ضبط جودة الصورة: $q", Toast.LENGTH_SHORT).show()
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(0xFF9333EA),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier.size(18.dp))
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = "الصورة > $q",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            fontSize = 13.5.sp
                                                        ),
                                                        color = if (isSelected) Color(0xFF9333EA) else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = desc,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Direct Open Export Screen Button
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = { viewModel.openExportScreenLatest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فتح شاشة التصدير والمعاينة ↗",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportElementToggleRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF16A34A),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

