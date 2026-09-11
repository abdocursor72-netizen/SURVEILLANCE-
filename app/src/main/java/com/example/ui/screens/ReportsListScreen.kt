package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.model.Report
import com.example.data.preferences.AppPreferences
import com.example.ui.ReportViewModel
import com.example.ui.ScreenDestination
import com.example.ui.SortOrder
import com.example.ui.components.SurveillanceTopBar
import com.example.ui.theme.AccentVirusRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GrayButtonBg
import com.example.ui.theme.GrayButtonBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ReportsListScreen(viewModel: ReportViewModel) {
    val context = LocalContext.current
    val reports by viewModel.displayedReports.collectAsState()
    val totalActiveCount by viewModel.activeCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isAutoRefreshEnabled by viewModel.isAutoRefreshEnabled.collectAsState()
    val lastRefreshTime by viewModel.lastRefreshTime.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<Report?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.triggerRefresh()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )
    val rotation = if (isRefreshing) spinAngle else 0f

    Scaffold(
        topBar = {
            SurveillanceTopBar(
                title = AppPreferences.APP_TITLE,
                titleFontSize = 14.sp,
                showBackButton = false,
                actions = {
                    IconButton(
                        onClick = { viewModel.triggerRefresh() },
                        modifier = Modifier.testTag("topbar_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث",
                            modifier = Modifier.rotate(rotation),
                            tint = if (isRefreshing) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.startNewReport() },
                        modifier = Modifier.testTag("topbar_add_report")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAdd,
                            contentDescription = "إنشاء تقرير",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier.testTag("topbar_search_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = if (isSearchActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.SETTINGS) },
                        modifier = Modifier.testTag("topbar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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
            // Live Search Bar
            AnimatedVisibility(visible = isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("ابحث عن قطاع، رقم حقل، ملاحظ...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("search_reports_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subheader: "التقارير" + "جديد" button (Matching Screenshot 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "جديد" Button (Emerald Green rounded button on the left)
                Button(
                    onClick = { viewModel.startNewReport() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("new_report_button")
                ) {
                    Text(
                        text = "جديد",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }

                // Header Title: "التقارير" on the right
                Text(
                    text = "التقارير",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sort icon + Auto-refresh badge + Saved reports count row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sort icon button
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sort_reports_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "ترتيب",
                            tint = TextSecondary
                        )
                    }

                    // Auto-refresh status badge
                    if (isAutoRefreshEnabled) {
                        val formattedTime = remember(lastRefreshTime) {
                            SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date(lastRefreshTime))
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isRefreshing) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isRefreshing) EmeraldPrimary.copy(alpha = 0.4f) else Color.Transparent
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.triggerRefresh() }
                                .testTag("auto_refresh_status_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isRefreshing) EmeraldPrimary else Color(0xFF10B981))
                                )
                                Text(
                                    text = if (isRefreshing) "تحديث مباشر..." else "تحديث تلقائي $formattedTime",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (isRefreshing) EmeraldPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Count text: "عدد التقارير المحفوظة: X"
                Text(
                    text = "عدد التقارير المحفوظة: $totalActiveCount",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Report Cards List
            if (reports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "لا توجد تقارير مطابقة للبحث" else "لا توجد تقارير حالياً",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اضغط على زر \"جديد\" للبدء في تدوين تقرير مراقبة جديد للحقل",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.startNewReport() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إنشاء تقرير الآن")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(reports, key = { it.id }) { report ->
                        ReportCardItem(
                            report = report,
                            onEdit = { viewModel.editReport(report) },
                            onShare = { viewModel.openExportScreen(report) },
                            onDelete = { reportToDelete = report },
                            onTogglePin = { viewModel.togglePin(report) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (reportToDelete != null) {
        val report = reportToDelete!!
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            title = { Text("حذف التقرير", fontWeight = FontWeight.Bold) },
            text = {
                Text("هل تريد نقل تقرير الحقل (${report.fieldNumber}) القطاع (${report.sector}) إلى سلة المهملات؟ يمكنك استعادته لاحقاً من الإعدادات.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReport(report)
                        reportToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentVirusRed),
                    modifier = Modifier.testTag("confirm_delete_report_button")
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Sort Menu Dialog
    if (showSortMenu) {
        AlertDialog(
            onDismissRequest = { showSortMenu = false },
            title = { Text("ترتيب التقارير حسب", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    SortOptionItem("الأحدث أولاً", sortOrder == SortOrder.NEWEST) {
                        viewModel.sortOrder.value = SortOrder.NEWEST
                        showSortMenu = false
                    }
                    SortOptionItem("الأقدم أولاً", sortOrder == SortOrder.OLDEST) {
                        viewModel.sortOrder.value = SortOrder.OLDEST
                        showSortMenu = false
                    }
                    SortOptionItem("رقم الحقل", sortOrder == SortOrder.FIELD_NUMBER) {
                        viewModel.sortOrder.value = SortOrder.FIELD_NUMBER
                        showSortMenu = false
                    }
                    SortOptionItem("القطاع", sortOrder == SortOrder.SECTOR) {
                        viewModel.sortOrder.value = SortOrder.SECTOR
                        showSortMenu = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortMenu = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun SortOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
            )
        }
    }
}

@Composable
fun ReportCardItem(
    report: Report,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (report.isPinned) EmeraldPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        shadowElevation = if (report.isPinned) 2.dp else 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report_card_${report.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row: Pin button on left, content in middle, Field on right (Matching Screenshot 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Pin button on far left
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("pin_report_${report.id}")
                ) {
                    Icon(
                        imageVector = if (report.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (report.isPinned) "إلغاء التثبيت" else "تثبيت",
                        tint = if (report.isPinned) EmeraldPrimary else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Center/Right details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "القطاع: ${report.sector}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تاريخ الملاحظة: ${report.observationDate}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "تاريخ حفظ الملاحظة: ${report.savedDate}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = TextSecondary
                    )
                }

                // Far right: "الحقل" + Big Number (e.g. "5")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "الحقل",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = report.fieldNumber,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row: "حذف" | "مشاركة" | "تعديل" (Matching Screenshot 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Delete button
                ActionPillButton(
                    text = "حذف",
                    icon = Icons.Default.Delete,
                    iconTint = AccentVirusRed,
                    modifier = Modifier.weight(1f),
                    testTag = "delete_report_${report.id}",
                    onClick = onDelete
                )

                // Share button
                ActionPillButton(
                    text = "مشاركة",
                    icon = Icons.Default.Share,
                    iconTint = TextSecondary,
                    modifier = Modifier.weight(1.1f),
                    testTag = "share_report_${report.id}",
                    onClick = onShare
                )

                // Edit button
                ActionPillButton(
                    text = "تعديل",
                    icon = Icons.Default.Edit,
                    iconTint = TextSecondary,
                    modifier = Modifier.weight(1.1f),
                    testTag = "edit_report_${report.id}",
                    onClick = onEdit
                )
            }
        }
    }
}

@Composable
fun ActionPillButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = GrayButtonBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrayButtonBorder),
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
