package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SampleItem
import com.example.ui.theme.AccentBeneficialGreen
import com.example.ui.theme.AccentPestOrange
import com.example.ui.theme.AccentVirusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SampleTableView(
    headerTitleMiddle: String, // "الشدة" or "المستوى" or "العدد"
    headerBackgroundColor: Color,
    items: List<SampleItem>,
    onValueClick: (SampleItem) -> Unit,
    onNotesClick: (SampleItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header Band
        Surface(
            color = headerBackgroundColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 3: Notes / Tracks
                Text(
                    text = "ملاحظات / مسارات",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1.2f)
                )

                // Column 2: Severity / Level / Count
                Text(
                    text = headerTitleMiddle,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.9f)
                )

                // Column 1: Sample Name
                Text(
                    text = "العينة",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = TextSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.3f)
                )
            }
        }

        // Table Rows
        items.forEachIndexed { index, item ->
            SampleTableRow(
                item = item,
                onValueClick = { onValueClick(item) },
                onNotesClick = { onNotesClick(item) }
            )
            if (index < items.size - 1) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
fun SampleTableRow(
    item: SampleItem,
    onValueClick: () -> Unit,
    onNotesClick: () -> Unit
) {
    val isPositive = item.value != "0/3" && item.value != "0"
    val badgeColor = when {
        !isPositive -> MaterialTheme.colorScheme.onSurface
        item.category == com.example.data.model.SampleCategory.VIRUS -> AccentVirusRed
        item.category == com.example.data.model.SampleCategory.PEST -> AccentPestOrange
        else -> AccentBeneficialGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column 3: Notes / Paths (clickable to edit)
        Box(
            modifier = Modifier
                .weight(1.2f)
                .clip(RoundedCornerShape(6.dp))
                .clickable { onNotesClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("notes_row_${item.id}"),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (item.notes.isNotBlank()) item.notes else "---",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp
                ),
                color = if (item.notes == "---") TextTertiary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Column 2: Severity / Count Badge (clickable to cycle)
        Box(
            modifier = Modifier
                .weight(0.9f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onValueClick() }
                .padding(vertical = 4.dp)
                .testTag("value_row_${item.id}"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isPositive) badgeColor.copy(alpha = 0.12f) else Color.Transparent
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = badgeColor
                )
            }
        }

        // Column 1: Sample Name (Arabic prominent + scientific subtitle)
        Column(
            modifier = Modifier
                .weight(1.3f)
                .padding(start = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.nameArabic,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.nameScientific.isNotBlank()) {
                Text(
                    text = item.nameScientific,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = TextTertiary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
