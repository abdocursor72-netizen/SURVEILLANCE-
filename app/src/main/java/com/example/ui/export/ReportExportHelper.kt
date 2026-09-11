package com.example.ui.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Report
import com.example.data.model.SampleCategory
import com.example.data.model.SampleItem
import com.example.data.preferences.AppPreferences
import java.io.File
import java.io.FileOutputStream

object ReportExportHelper {

    fun generateTextReport(report: Report): String {
        val items = SampleItem.listFromJson(report.samplesJson)
        val viruses = items.filter { it.category == SampleCategory.VIRUS }
        val pests = items.filter { it.category == SampleCategory.PEST }
        val beneficials = items.filter { it.category == SampleCategory.BENEFICIAL }

        val sb = StringBuilder()
        sb.append("RAPPORT ET OBSERVATION\n")
        sb.append("════════════════════════════════════════\n")
        sb.append("OBSERVATEUR : ${report.observerName.ifBlank { "FOUAD EL..." }}\n")
        sb.append("DATE        : ${report.observationDate}\n")
        sb.append("SECTEUR     : ${report.sector} | ${report.fieldNumber}\n")
        sb.append("════════════════════════════════════════\n\n")

        sb.append("| 1. Virus\n")
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-22s %-8s %-10s\n", "Type de Virus", "Total", "Lignes/Obs"))
        var virusTotal = 0
        viruses.forEach {
            val count = it.value.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            virusTotal += count
            sb.append(String.format("%-22s %-8s %-10s\n", it.nameScientific.ifBlank { it.nameArabic }, it.value, if (it.notes.isBlank() || it.notes == "---") "-" else it.notes))
        }
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-22s %-8d\n\n", "TOTAL DES VIRUS", virusTotal))

        sb.append("| 2. Ravageurs et Maladies\n")
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-22s %-8s %-10s\n", "Ravageurs", "Gravité", "Lignes/Obs"))
        pests.forEach {
            sb.append(String.format("%-22s %-8s %-10s\n", it.nameScientific.ifBlank { it.nameArabic }, it.value, if (it.notes.isBlank() || it.notes == "---") "-" else it.notes))
        }
        sb.append("\n")

        sb.append("| 3. Auxiliaires\n")
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-22s %-8s %-10s\n", "Auxiliaires", "Niveau", "Lignes/Obs"))
        beneficials.forEach {
            sb.append(String.format("%-22s %-8s %-10s\n", it.nameScientific.ifBlank { it.nameArabic }, it.value, if (it.notes.isBlank() || it.notes == "---") "-" else it.notes))
        }
        sb.append("\n")

        if (report.specialNotes.isNotBlank()) {
            sb.append("| 4. Remarques Spéciales\n")
            sb.append("----------------------------------------\n")
            sb.append("${report.specialNotes}\n\n")
        }

        if (report.diseaseNote.isNotBlank() || report.imageUri.isNotBlank()) {
            sb.append("| 5. Photo & Diagnostic Culture\n")
            sb.append("----------------------------------------\n")
            if (report.diseaseNote.isNotBlank()) {
                sb.append("Diagnostic: ${report.diseaseNote}\n")
            }
            if (report.imageUri.isNotBlank()) {
                sb.append("Photo de terrain: Attachée au rapport\n")
            }
            sb.append("\n")
        }

        sb.append("════════════════════════════════════════\n")
        sb.append("${AppPreferences.APP_TITLE} • ${AppPreferences.DEVELOPER_NAME}\n")
        return sb.toString()
    }

    fun copyToClipboard(context: Context, report: Report) {
        val text = generateTextReport(report)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Rapport ${AppPreferences.APP_TITLE}", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم نسخ التقرير إلى الحافظة بنجاح 📋", Toast.LENGTH_SHORT).show()
    }

    fun shareAsFile(context: Context, report: Report) {
        try {
            val text = generateTextReport(report)
            val fileName = "rapport_${report.sector}_${report.fieldNumber}.txt"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { it.write(text.toByteArray(Charsets.UTF_8)) }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rapport SF-Surveillance ${report.fieldNumber}")
                putExtra(Intent.EXTRA_TEXT, "تصدير تقرير الحقل ${report.fieldNumber} (${report.sector})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "تصدير الملف عبر")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "حدث خطأ أثناء تصدير الملف: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareAsPdf(context: Context, report: Report) {
        try {
            val items = SampleItem.listFromJson(report.samplesJson)
            val viruses = items.filter { it.category == SampleCategory.VIRUS }
            val pests = items.filter { it.category == SampleCategory.PEST }
            val beneficials = items.filter { it.category == SampleCategory.BENEFICIAL }

            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Paints
            val bgPaint = Paint().apply { color = Color.WHITE }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            val titlePaint = Paint().apply {
                color = Color.parseColor("#166534")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val greenBarPaint = Paint().apply {
                color = Color.parseColor("#166534")
                style = Paint.Style.FILL
            }

            val labelPaint = Paint().apply {
                color = Color.parseColor("#64748B")
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val valuePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val sectionTitlePaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val tableHeaderBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }

            val tableHeaderPaint = Paint().apply {
                color = Color.parseColor("#475569")
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val tableRowPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                strokeWidth = 1f
            }

            var y = 45f

            // Title
            canvas.drawText("RAPPORT ET OBSERVATION", (pageWidth / 2).toFloat(), y, titlePaint)
            y += 8f
            canvas.drawRoundRect(RectF((pageWidth / 2 - 60).toFloat(), y, (pageWidth / 2 + 60).toFloat(), y + 3f), 2f, 2f, greenBarPaint)
            y += 28f

            // Metadata Row: Observateur | Date | Secteur
            canvas.drawText("OBSERVATEUR", 40f, y, labelPaint)
            canvas.drawText("DATE", 240f, y, labelPaint)
            canvas.drawText("SECTEUR |", 440f, y, labelPaint)
            y += 14f

            canvas.drawText(report.observerName.ifBlank { "FOUAD EL..." }, 40f, y, valuePaint)
            canvas.drawText(report.observationDate, 240f, y, valuePaint)
            canvas.drawText("${report.sector} | ${report.fieldNumber}", 440f, y, valuePaint)
            y += 20f

            canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, linePaint)
            y += 18f

            // Helper function to draw table
            fun drawSectionTable(
                sectionNum: Int,
                title: String,
                accentColorHex: String,
                col1Name: String,
                col2Name: String,
                itemsList: List<SampleItem>,
                isVirusSection: Boolean = false
            ) {
                // Section Bar and Title
                val barPaint = Paint().apply { color = Color.parseColor(accentColorHex) }
                canvas.drawRoundRect(RectF(40f, y - 10f, 44f, y + 4f), 2f, 2f, barPaint)
                sectionTitlePaint.color = Color.parseColor(accentColorHex)
                canvas.drawText("$sectionNum. $title", 52f, y, sectionTitlePaint)
                y += 10f

                // Table Header Box
                canvas.drawRoundRect(RectF(40f, y, (pageWidth - 40).toFloat(), y + 18f), 4f, 4f, tableHeaderBgPaint)
                val hY = y + 12f
                canvas.drawText(col1Name, 50f, hY, tableHeaderPaint)
                canvas.drawText(col2Name, 260f, hY, tableHeaderPaint)
                canvas.drawText("Lignes/Observation", 420f, hY, tableHeaderPaint)
                y += 22f

                // Rows
                itemsList.forEach { item ->
                    val name = item.nameScientific.ifBlank { item.nameArabic }
                    val obs = if (item.notes.isBlank() || item.notes == "---") "-" else item.notes
                    canvas.drawText(name, 50f, y + 10f, tableRowPaint)
                    canvas.drawText(item.value, 260f, y + 10f, tableRowPaint)
                    canvas.drawText(obs, 420f, y + 10f, tableRowPaint)
                    y += 16f
                    canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, linePaint)
                    y += 4f
                }

                if (isVirusSection) {
                    var vTotal = 0
                    itemsList.forEach {
                        vTotal += it.value.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
                    }
                    val totalPaint = Paint().apply {
                        color = Color.parseColor(accentColorHex)
                        textSize = 10f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText("TOTAL DES VIRUS", 50f, y + 10f, totalPaint)
                    canvas.drawText("$vTotal", 260f, y + 10f, totalPaint)
                    y += 18f
                }

                y += 12f
            }

            // 1. Virus
            drawSectionTable(
                sectionNum = 1,
                title = "Virus",
                accentColorHex = "#DC2626",
                col1Name = "Type de Virus",
                col2Name = "Total",
                itemsList = viruses,
                isVirusSection = true
            )

            // 2. Ravageurs et Maladies
            drawSectionTable(
                sectionNum = 2,
                title = "Ravageurs et Maladies",
                accentColorHex = "#D97706",
                col1Name = "Ravageurs",
                col2Name = "Gravité",
                itemsList = pests
            )

            // 3. Auxiliaires
            drawSectionTable(
                sectionNum = 3,
                title = "Auxiliaires",
                accentColorHex = "#16A34A",
                col1Name = "Auxiliaires",
                col2Name = "Niveau",
                itemsList = beneficials
            )

            // Notes if any
            if (report.specialNotes.isNotBlank()) {
                val barPaint = Paint().apply { color = Color.parseColor("#2563EB") }
                canvas.drawRoundRect(RectF(40f, y - 10f, 44f, y + 4f), 2f, 2f, barPaint)
                sectionTitlePaint.color = Color.parseColor("#2563EB")
                canvas.drawText("4. Remarques Spéciales", 52f, y, sectionTitlePaint)
                y += 14f
                canvas.drawText(report.specialNotes, 50f, y, tableRowPaint)
                y += 20f
            }

            // Footer
            val footerPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 9f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawLine(40f, (pageHeight - 40).toFloat(), (pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), linePaint)
            canvas.drawText("${AppPreferences.APP_TITLE} • ${AppPreferences.DEVELOPER_NAME}", (pageWidth / 2).toFloat(), (pageHeight - 25).toFloat(), footerPaint)

            pdfDocument.finishPage(page)

            val fileName = "rapport_${report.sector}_${report.fieldNumber}.pdf"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rapport PDF ${AppPreferences.APP_TITLE} ${report.fieldNumber}")
                putExtra(Intent.EXTRA_TEXT, "مرفق تقرير الحقل ${report.fieldNumber} بصيغة PDF")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "تصدير التقرير PDF عبر")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "حدث خطأ أثناء إنشاء PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareAsImage(context: Context, report: Report) {
        try {
            val items = SampleItem.listFromJson(report.samplesJson)
            val viruses = items.filter { it.category == SampleCategory.VIRUS }
            val pests = items.filter { it.category == SampleCategory.PEST }
            val beneficials = items.filter { it.category == SampleCategory.BENEFICIAL }

            val width = 1080
            val height = 1600
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Background
            val bgPaint = Paint().apply { color = Color.parseColor("#F8FAFC") }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // Card Container
            val cardPaint = Paint().apply { color = Color.WHITE }
            val cardRect = RectF(40f, 40f, (width - 40).toFloat(), (height - 40).toFloat())
            canvas.drawRoundRect(cardRect, 24f, 24f, cardPaint)

            val titlePaint = Paint().apply {
                color = Color.parseColor("#166534")
                textSize = 36f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val greenBarPaint = Paint().apply {
                color = Color.parseColor("#166534")
                style = Paint.Style.FILL
            }

            val labelPaint = Paint().apply {
                color = Color.parseColor("#64748B")
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val valuePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 26f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val sectionTitlePaint = Paint().apply {
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val tableHeaderBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }

            val tableHeaderPaint = Paint().apply {
                color = Color.parseColor("#475569")
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val tableRowPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                strokeWidth = 2f
            }

            var y = 120f

            // Title
            canvas.drawText("RAPPORT ET OBSERVATION", (width / 2).toFloat(), y, titlePaint)
            y += 18f
            canvas.drawRoundRect(RectF((width / 2 - 120).toFloat(), y, (width / 2 + 120).toFloat(), y + 6f), 4f, 4f, greenBarPaint)
            y += 60f

            // Metadata Row
            canvas.drawText("OBSERVATEUR", 80f, y, labelPaint)
            canvas.drawText("DATE", 450f, y, labelPaint)
            canvas.drawText("SECTEUR |", 800f, y, labelPaint)
            y += 34f

            canvas.drawText(report.observerName.ifBlank { "FOUAD EL..." }, 80f, y, valuePaint)
            canvas.drawText(report.observationDate, 450f, y, valuePaint)
            canvas.drawText("${report.sector} | ${report.fieldNumber}", 800f, y, valuePaint)
            y += 40f

            canvas.drawLine(80f, y, (width - 80).toFloat(), y, linePaint)
            y += 40f

            fun drawImgSection(
                sectionNum: Int,
                title: String,
                accentColorHex: String,
                col1Name: String,
                col2Name: String,
                itemsList: List<SampleItem>,
                isVirusSection: Boolean = false
            ) {
                val barPaint = Paint().apply { color = Color.parseColor(accentColorHex) }
                canvas.drawRoundRect(RectF(80f, y - 24f, 88f, y + 8f), 4f, 4f, barPaint)
                sectionTitlePaint.color = Color.parseColor(accentColorHex)
                canvas.drawText("$sectionNum. $title", 106f, y, sectionTitlePaint)
                y += 24f

                canvas.drawRoundRect(RectF(80f, y, (width - 80).toFloat(), y + 42f), 8f, 8f, tableHeaderBgPaint)
                val hY = y + 30f
                canvas.drawText(col1Name, 100f, hY, tableHeaderPaint)
                canvas.drawText(col2Name, 520f, hY, tableHeaderPaint)
                canvas.drawText("Lignes/Observation", 780f, hY, tableHeaderPaint)
                y += 56f

                itemsList.forEach { item ->
                    val name = item.nameScientific.ifBlank { item.nameArabic }
                    val obs = if (item.notes.isBlank() || item.notes == "---") "-" else item.notes
                    canvas.drawText(name, 100f, y + 16f, tableRowPaint)
                    canvas.drawText(item.value, 520f, y + 16f, tableRowPaint)
                    canvas.drawText(obs, 780f, y + 16f, tableRowPaint)
                    y += 32f
                    canvas.drawLine(80f, y, (width - 80).toFloat(), y, linePaint)
                    y += 10f
                }

                if (isVirusSection) {
                    var vTotal = 0
                    itemsList.forEach {
                        vTotal += it.value.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
                    }
                    val totalPaint = Paint().apply {
                        color = Color.parseColor(accentColorHex)
                        textSize = 22f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText("TOTAL DES VIRUS", 100f, y + 20f, totalPaint)
                    canvas.drawText("$vTotal", 520f, y + 20f, totalPaint)
                    y += 38f
                }

                y += 24f
            }

            drawImgSection(1, "Virus", "#DC2626", "Type de Virus", "Total", viruses, true)
            drawImgSection(2, "Ravageurs et Maladies", "#D97706", "Ravageurs", "Gravité", pests)
            drawImgSection(3, "Auxiliaires", "#16A34A", "Auxiliaires", "Niveau", beneficials)

            // Footer
            val footerPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawLine(80f, (height - 90).toFloat(), (width - 80).toFloat(), (height - 90).toFloat(), linePaint)
            canvas.drawText("${AppPreferences.APP_TITLE} • ${AppPreferences.DEVELOPER_NAME}", (width / 2).toFloat(), (height - 55).toFloat(), footerPaint)

            val fileName = "rapport_${report.sector}_${report.fieldNumber}.png"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rapport Image ${AppPreferences.APP_TITLE} ${report.fieldNumber}")
                putExtra(Intent.EXTRA_TEXT, "تقرير الحقل ${report.fieldNumber} كصورة")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "تصدير صورة التقرير عبر")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "حدث خطأ أثناء تصدير الصورة: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
