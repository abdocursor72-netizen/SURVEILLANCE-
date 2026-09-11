package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Report
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import com.example.ui.ReportViewModel
import com.example.ui.export.ReportExportHelper
import com.example.ui.theme.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.io.File

@Composable
fun ExportReportScreen(
    viewModel: ReportViewModel
) {
    val context = LocalContext.current
    val targetReport by viewModel.exportReportTarget.collectAsState()

    // Default fallback report if none set
    val report = targetReport ?: remember {
        Report(
            fieldNumber = "5",
            sector = "BLa",
            observerName = "FOUAD EL...",
            observationDate = "10/09/2026",
            samplesJson = SampleItem.listToJson(SampleItem.getDefaultSamples())
        )
    }

    val sampleItems = remember(report.samplesJson) {
        val parsed = SampleItem.listFromJson(report.samplesJson)
        if (parsed.isNotEmpty()) parsed else SampleItem.getDefaultSamples()
    }

    val viruses = remember(sampleItems) { sampleItems.filter { it.category == SampleCategory.VIRUS } }
    val pests = remember(sampleItems) { sampleItems.filter { it.category == SampleCategory.PEST } }
    val beneficials = remember(sampleItems) { sampleItems.filter { it.category == SampleCategory.BENEFICIAL } }

    var showExportSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Custom Top Bar exactly like Screenshot: "OBSERVER" (bold left) + Back arrow (right)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "surveillance-maladie" in bold letters on left
                    Text(
                        text = AppPreferences.APP_TITLE,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("export_observer_logo")
                    )

                    // Back Arrow icon on the right
                    IconButton(
                        onClick = { viewModel.navigateBackFromExport() },
                        modifier = Modifier.testTag("export_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Row: Left = Settings gear, Center = "الحقل: X", Right = "التصدير"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings icon on the left
                IconButton(
                    onClick = { showExportSettingsDialog = true },
                    modifier = Modifier.testTag("export_settings_gear")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "خيارات التصدير",
                        tint = TextSecondary
                    )
                }

                // Center: "الحقل: X"
                Text(
                    text = "الحقل: ${report.fieldNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("export_field_number_text")
                )

                // Right: "التصدير"
                Text(
                    text = "التصدير",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("export_headline_title")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Action Buttons Row: "صورة" | "PDF" | "نسخ" | "ملف"
            // Matching the exact screenshot cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: صورة (Photo/Image)
                ExportActionSquareButton(
                    title = "صورة",
                    icon = Icons.Default.Image,
                    iconTint = EmeraldPrimary,
                    testTag = "export_btn_image",
                    onClick = {
                        ReportExportHelper.shareAsImage(context, report)
                    }
                )

                // Button 2: PDF
                ExportActionSquareButton(
                    title = "PDF",
                    icon = Icons.Default.PictureAsPdf,
                    iconTint = AccentVirusRed,
                    testTag = "export_btn_pdf",
                    onClick = {
                        ReportExportHelper.shareAsPdf(context, report)
                    }
                )

                // Button 3: نسخ (Copy)
                ExportActionSquareButton(
                    title = "نسخ",
                    icon = Icons.Default.ContentCopy,
                    iconTint = AccentInfoBlue,
                    testTag = "export_btn_copy",
                    onClick = {
                        ReportExportHelper.copyToClipboard(context, report)
                    }
                )

                // Button 4: ملف (File)
                ExportActionSquareButton(
                    title = "ملف",
                    icon = Icons.Default.InsertDriveFile,
                    iconTint = AccentPestOrange,
                    testTag = "export_btn_file",
                    onClick = {
                        ReportExportHelper.shareAsFile(context, report)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The Document Sheet Preview (Clean white paper style matching screenshot)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_document_paper")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Document Title: "RAPPORT ET OBSERVATION"
                        Text(
                            text = "RAPPORT ET OBSERVATION",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFF166534), // Forest Green
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Green underline bar
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(120.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF166534))
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Metadata Row: Observateur | Date | Secteur |
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "OBSERVATEUR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = report.observerName.ifBlank { "FOUAD EL..." },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DATE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = report.observationDate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "SECTEUR |",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${report.sector} | ${report.fieldNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Virus Section
                        if (viewModel.prefs.exportIncludeViruses) {
                            ReportDocSection(
                                number = 1,
                                title = "Virus",
                                barColor = Color(0xFFDC2626),
                                col1Header = "Type de Virus",
                                col2Header = "Total",
                                col3Header = "Lignes/Observation",
                                items = viruses,
                                isVirusSection = true
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // 2. Ravageurs et Maladies Section
                        if (viewModel.prefs.exportIncludePests) {
                            ReportDocSection(
                                number = 2,
                                title = "Ravageurs et Maladies",
                                barColor = Color(0xFFD97706),
                                col1Header = "Ravageurs",
                                col2Header = "Gravité",
                                col3Header = "Lignes/Observation",
                                items = pests
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // 3. Auxiliaires Section
                        if (viewModel.prefs.exportIncludeBeneficials) {
                            ReportDocSection(
                                number = 3,
                                title = "Auxiliaires",
                                barColor = Color(0xFF16A34A),
                                col1Header = "Auxiliaires",
                                col2Header = "Niveau",
                                col3Header = "Lignes/Observation",
                                items = beneficials
                            )
                        }

                        // 4. Remarques Spéciales (if any and enabled)
                        if (viewModel.prefs.exportIncludeSpecialNotes && report.specialNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(Color(0xFF2563EB))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "4. Remarques Spéciales",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = report.specialNotes,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // 5. Photo & Diagnostic Culture (if attached)
                        if (report.imageUri.isNotBlank() && File(report.imageUri).exists()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(Color(0xFF059669))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "5. Photo & Diagnostic Culture",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    AsyncImage(
                                        model = File(report.imageUri),
                                        contentDescription = "Photo de la culture",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (report.diseaseNote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Diagnostic: ${report.diseaseNote}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Footer branding
                        Text(
                            text = "${AppPreferences.APP_TITLE} • ${AppPreferences.DEVELOPER_NAME}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Export Options Dialog (opened by gear icon)
    if (showExportSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showExportSettingsDialog = false },
            title = {
                Text(
                    text = "خيارات التصدير والمشاركة",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اختر الإجراء المطلوب لتصدير بيانات الحقل ${report.fieldNumber}:",
                        fontSize = 14.sp
                    )

                    ListItem(
                        headlineContent = { Text("مشاركة عبر تطبيقات أخرى (واتساب، إلخ)") },
                        leadingContent = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = AccentVirusRed) },
                        modifier = Modifier.clickable {
                            showExportSettingsDialog = false
                            ReportExportHelper.shareAsPdf(context, report)
                        }
                    )

                    ListItem(
                        headlineContent = { Text("تصدير كملف نصي") },
                        leadingContent = { Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = AccentPestOrange) },
                        modifier = Modifier.clickable {
                            showExportSettingsDialog = false
                            ReportExportHelper.shareAsFile(context, report)
                        }
                    )

                    ListItem(
                        headlineContent = { Text("نسخ النص الكامل للتقرير") },
                        leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AccentInfoBlue) },
                        modifier = Modifier.clickable {
                            showExportSettingsDialog = false
                            ReportExportHelper.copyToClipboard(context, report)
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportSettingsDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun ExportActionSquareButton(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
            .testTag(testTag)
    ) {
        // Rounded Square Box
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.size(62.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title Label
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

@Composable
fun ReportDocSection(
    number: Int,
    title: String,
    barColor: Color,
    col1Header: String,
    col2Header: String,
    col3Header: String,
    items: List<SampleItem>,
    isVirusSection: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title with colored bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(barColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$number. $title",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }

        // Table Header
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = col1Header,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = col2Header,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = col3Header,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Table Rows
        items.forEachIndexed { index, item ->
            val displayName = item.nameScientific.ifBlank { item.nameArabic }
            val displayObs = if (item.notes.isBlank() || item.notes == "---") "-" else item.notes

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = item.value,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = displayObs,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.5f)
                )
            }

            if (index < items.lastIndex) {
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }

        // Summary row for viruses if virus section
        if (isVirusSection) {
            val totalVirusCount = items.sumOf { it.value.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL DES VIRUS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = barColor,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = "$totalVirusCount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = barColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1.5f))
            }
        }
    }
}
