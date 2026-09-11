package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import com.example.ui.ReportViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AccordionCard
import com.example.ui.components.SampleTableView
import com.example.ui.components.SurveillanceTopBar
import com.example.ui.theme.AccentBeneficialGreen
import com.example.ui.theme.AccentInfoBlue
import com.example.ui.theme.AccentNotesAmber
import com.example.ui.theme.AccentPestOrange
import com.example.ui.theme.AccentVirusRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GrayButtonBg
import com.example.ui.theme.GrayButtonBorder
import com.example.ui.theme.TableHeaderBeneficial
import com.example.ui.theme.TableHeaderPest
import com.example.ui.theme.TableHeaderVirus
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ReportEditorScreen(viewModel: ReportViewModel) {
    val context = LocalContext.current

    val observerName by viewModel.formObserverName.collectAsState()
    val sector by viewModel.formSector.collectAsState()
    val fieldNumber by viewModel.formFieldNumber.collectAsState()
    val observationDate by viewModel.formObservationDate.collectAsState()
    val specialNotes by viewModel.formSpecialNotes.collectAsState()
    val samples by viewModel.formSamples.collectAsState()

    val isBasicInfoExpanded by viewModel.isBasicInfoExpanded.collectAsState()
    val isVirusesExpanded by viewModel.isVirusesExpanded.collectAsState()
    val isPestsExpanded by viewModel.isPestsExpanded.collectAsState()
    val isBeneficialExpanded by viewModel.isBeneficialExpanded.collectAsState()
    val isSpecialNotesExpanded by viewModel.isSpecialNotesExpanded.collectAsState()
    val isPhotoExpanded by viewModel.isPhotoExpanded.collectAsState()

    val imageUri by viewModel.formImageUri.collectAsState()
    val diseaseNote by viewModel.formDiseaseNote.collectAsState()

    var isViewingFullPhoto by remember { mutableStateOf(false) }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = saveImageToInternalStorage(context, uri)
            if (savedPath != null) {
                viewModel.setImageUri(savedPath)
            } else {
                Toast.makeText(context, "تعذر تحميل الصورة المحددة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val isEditing = viewModel.editingReportId != 0L

    // Dialog state for editing row notes
    var editingSampleItem by remember { mutableStateOf<SampleItem?>(null) }
    var tempNoteText by remember { mutableStateOf("") }

    // Dialog state for editing count value
    var editingCountItem by remember { mutableStateOf<SampleItem?>(null) }
    var tempCountValue by remember { mutableStateOf(0) }

    val viruses = remember(samples) { samples.filter { it.category == SampleCategory.VIRUS } }
    val pests = remember(samples) { samples.filter { it.category == SampleCategory.PEST } }
    val beneficials = remember(samples) { samples.filter { it.category == SampleCategory.BENEFICIAL } }

    val isSaveEnabled = sector.isNotBlank() && fieldNumber.isNotBlank()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Subheader: "إنشاء تقرير" (Right) + "حفظ" & Share (Left)
                // Exactly matching Screenshots 1, 3, 4, 6, 7
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Save button & Share button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Save button
                        Button(
                            onClick = {
                                if (isSaveEnabled) {
                                    viewModel.saveCurrentReport {
                                        Toast.makeText(context, "تم حفظ التقرير بنجاح", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "يرجى إدخال القطاع ورقم الحقل", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSaveEnabled) EmeraldPrimary else GrayButtonBorder,
                                contentColor = if (isSaveEnabled) Color.White else TextTertiary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("save_report_button")
                        ) {
                            Text(
                                text = "حفظ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        // Share icon button
                        IconButton(
                            onClick = {
                                val dummy = com.example.data.model.Report(
                                    observerName = observerName,
                                    sector = sector,
                                    fieldNumber = fieldNumber,
                                    observationDate = observationDate,
                                    savedDate = observationDate,
                                    specialNotes = specialNotes,
                                    samplesJson = SampleItem.listToJson(samples)
                                )
                                viewModel.openExportScreen(dummy)
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("share_editor_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "مشاركة التقرير",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Right: Title
                    Text(
                        text = if (isEditing) "تعديل تقرير" else "إنشاء تقرير",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Accordion 1: المعلومات الأساسية (Basic Info)
            item {
                AccordionCard(
                    title = "المعلومات الأساسية",
                    icon = Icons.Default.Assignment,
                    iconColor = AccentInfoBlue,
                    isExpanded = isBasicInfoExpanded,
                    onToggle = { viewModel.isBasicInfoExpanded.value = !isBasicInfoExpanded }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // اسم الملاحظ (Observer Name)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "اسم الملاحظ",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = TextSecondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = observerName,
                                onValueChange = { viewModel.formObserverName.value = it },
                                placeholder = {
                                    Text(
                                        "ادخل اسم الملاحظ",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("observer_name_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GrayButtonBg,
                                    unfocusedContainerColor = GrayButtonBg
                                )
                            )
                        }

                        // Row: القطاع (Secteur) & رقم الحقل (Matching Screenshot 6)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // رقم الحقل (Field Number) - Left column
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "رقم الحقل",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = TextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = fieldNumber,
                                    onValueChange = { viewModel.formFieldNumber.value = it },
                                    placeholder = {
                                        Text(
                                            "00",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("field_number_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = GrayButtonBg,
                                        unfocusedContainerColor = GrayButtonBg
                                    )
                                )
                            }

                            // القطاع (Secteur) - Right column
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "القطاع (Secteur)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = TextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = sector,
                                    onValueChange = { viewModel.formSector.value = it },
                                    placeholder = {
                                        Text(
                                            "رقم القطاع",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sector_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = GrayButtonBg,
                                        unfocusedContainerColor = GrayButtonBg
                                    )
                                )
                            }
                        }

                        // تاريخ الملاحظة (Observation Date)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "تاريخ الملاحظة",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = TextSecondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = observationDate,
                                onValueChange = { viewModel.formObservationDate.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("observation_date_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = GrayButtonBg,
                                    unfocusedContainerColor = GrayButtonBg
                                )
                            )
                        }
                    }
                }
            }

            // Accordion 2: الفيروسات (Viruses)
            item {
                AccordionCard(
                    title = "الفيروسات",
                    icon = Icons.Default.BugReport,
                    iconColor = AccentVirusRed,
                    isExpanded = isVirusesExpanded,
                    onToggle = { viewModel.isVirusesExpanded.value = !isVirusesExpanded }
                ) {
                    SampleTableView(
                        headerTitleMiddle = "العدد",
                        headerBackgroundColor = TableHeaderVirus,
                        items = viruses,
                        onValueClick = { item ->
                            // Open counter dialog for viruses
                            editingCountItem = item
                            tempCountValue = item.value.toIntOrNull() ?: 0
                        },
                        onNotesClick = { item ->
                            editingSampleItem = item
                            tempNoteText = if (item.notes == "---") "" else item.notes
                        }
                    )
                }
            }

            // Accordion 3: الآفات (Pests)
            item {
                AccordionCard(
                    title = "الآفات",
                    icon = Icons.Default.PestControl,
                    iconColor = AccentPestOrange,
                    isExpanded = isPestsExpanded,
                    onToggle = { viewModel.isPestsExpanded.value = !isPestsExpanded }
                ) {
                    SampleTableView(
                        headerTitleMiddle = "الشدة",
                        headerBackgroundColor = TableHeaderPest,
                        items = pests,
                        onValueClick = { item ->
                            viewModel.cycleSampleSeverity(item.id)
                        },
                        onNotesClick = { item ->
                            editingSampleItem = item
                            tempNoteText = if (item.notes == "---") "" else item.notes
                        }
                    )
                }
            }

            // Accordion 4: الحشرات النافعة (Beneficial Insects)
            item {
                AccordionCard(
                    title = "الحشرات النافعة",
                    icon = Icons.Default.Security,
                    iconColor = AccentBeneficialGreen,
                    isExpanded = isBeneficialExpanded,
                    onToggle = { viewModel.isBeneficialExpanded.value = !isBeneficialExpanded }
                ) {
                    SampleTableView(
                        headerTitleMiddle = "المستوى",
                        headerBackgroundColor = TableHeaderBeneficial,
                        items = beneficials,
                        onValueClick = { item ->
                            viewModel.cycleSampleSeverity(item.id)
                        },
                        onNotesClick = { item ->
                            editingSampleItem = item
                            tempNoteText = if (item.notes == "---") "" else item.notes
                        }
                    )
                }
            }

            // Accordion 5: صورة المحصول والتشخيص (Crop Photo & Diagnosis)
            item {
                AccordionCard(
                    title = if (imageUri.isNotBlank()) "صورة المحصول والتشخيص (مرفقة ✓)" else "صورة المحصول والتشخيص",
                    icon = Icons.Default.PhotoCamera,
                    iconColor = if (imageUri.isNotBlank()) Color(0xFF2E7D32) else AccentInfoBlue,
                    isExpanded = isPhotoExpanded,
                    onToggle = { viewModel.isPhotoExpanded.value = !isPhotoExpanded }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (imageUri.isBlank()) {
                            // Empty State with Camera & Upload buttons
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GrayButtonBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Text(
                                        text = "التقاط أو رفع صورة لتشخيص أمراض المحصول",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "وجّه الكاميرا نحو الأوراق أو الثمار المصابة أو ارفع صورة لتشخيص وتوثيق الحالة في التقرير.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.openCropCamera() },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("open_camera_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("فتح الكاميرا", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                galleryPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("upload_image_button"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("رفع من المعرض", fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Image attached state
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = File(imageUri),
                                    contentDescription = "صورة المحصول المرفقة",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clickable { isViewingFullPhoto = true }
                                        .testTag("attached_crop_image"),
                                    contentScale = ContentScale.Crop
                                )

                                // Overlay badge: View Fullscreen
                                Surface(
                                    color = Color(0x99000000),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .clickable { isViewingFullPhoto = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = "تكبير",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("تكبير", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Actions Row: Retake with camera, Replace from gallery, Remove
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.openCropCamera() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("التقاط أخرى", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        galleryPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("استبدال", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.clearImageUri() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentVirusRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("delete_image_button")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف الصورة", modifier = Modifier.size(16.dp))
                                }
                            }

                            // Disease Diagnosis Note Field
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "تشخيص وملاحظات صورة المحصول",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = TextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = diseaseNote,
                                    onValueChange = { viewModel.setDiseaseNote(it) },
                                    placeholder = {
                                        Text(
                                            "مثال: أعراض لفحة مبكرة على الأوراق، تبقع، أو عفن ثمار...",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("disease_note_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = GrayButtonBg,
                                        unfocusedContainerColor = GrayButtonBg
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Disease Diagnosis tags
                                val quickDiagnoses = listOf(
                                    "أعراض لفحة متأخرة",
                                    "تبقع الأوراق",
                                    "فيروس تجعد الأوراق",
                                    "إصابة بالذبابة البيضاء",
                                    "عفن رمادي (بوتريتس)",
                                    "سليم / فحص دوري"
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(quickDiagnoses) { diag ->
                                        AssistChip(
                                            onClick = {
                                                val updated = if (diseaseNote.isBlank()) diag else "$diseaseNote - $diag"
                                                viewModel.setDiseaseNote(updated)
                                            },
                                            label = { Text(diag, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Accordion 6: الملاحظات الخاصة (Special Notes)
            item {
                AccordionCard(
                    title = "الملاحظات الخاصة",
                    icon = Icons.Default.MenuBook,
                    iconColor = AccentNotesAmber,
                    isExpanded = isSpecialNotesExpanded,
                    onToggle = { viewModel.isSpecialNotesExpanded.value = !isSpecialNotesExpanded }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = specialNotes,
                            onValueChange = { viewModel.formSpecialNotes.value = it },
                            placeholder = {
                                Text(
                                    "اكتب الملاحظات والتفاصيل الخاصة بك .",
                                    color = TextTertiary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("special_notes_input"),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GrayButtonBg,
                                unfocusedContainerColor = GrayButtonBg
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Notes / Paths Edit Dialog
    if (editingSampleItem != null) {
        val currentItem = editingSampleItem!!
        val quickSuggestions = listOf("مسار 1", "مسار 2", "مسار 3", "بؤرة إصابة", "أعراض مبكرة", "سليم", "مسح")

        AlertDialog(
            onDismissRequest = { editingSampleItem = null },
            title = {
                Text(
                    text = "ملاحظات / مسارات: ${currentItem.nameArabic}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "أدخل رقم المسار أو الملاحظة الميدانية:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = tempNoteText,
                        onValueChange = { tempNoteText = it },
                        placeholder = { Text("مثال: مسار 4، بقع دقيقة...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sample_notes_text_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Quick suggestion chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickSuggestions) { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GrayButtonBg,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, GrayButtonBorder),
                                modifier = Modifier.clickable {
                                    tempNoteText = if (suggestion == "مسح") "" else suggestion
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (suggestion == "مسح") AccentVirusRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSampleNotes(currentItem.id, tempNoteText)
                        editingSampleItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSampleItem = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Virus Count Stepper Dialog
    if (editingCountItem != null) {
        val currentItem = editingCountItem!!
        AlertDialog(
            onDismissRequest = { editingCountItem = null },
            title = {
                Text(
                    text = "تحديد العدد: ${currentItem.nameArabic}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (tempCountValue > 0) tempCountValue-- },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GrayButtonBg, contentColor = TextPrimary),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Text(
                        text = "$tempCountValue",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { tempCountValue++ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSampleValue(currentItem.id, tempCountValue.toString())
                        editingCountItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCountItem = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Fullscreen Attached Image Viewer Dialog
    if (isViewingFullPhoto && imageUri.isNotBlank()) {
        Dialog(
            onDismissRequest = { isViewingFullPhoto = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = File(imageUri),
                    contentDescription = "معاينة صورة المحصول كاملة",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { isViewingFullPhoto = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(Color(0x88000000), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
