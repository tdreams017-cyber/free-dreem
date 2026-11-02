package com.example.ui.views

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journal.LucidDreamRecord
import com.example.journal.LucidJournalRepository
import com.example.ui.theme.DreamAccent
import com.example.ui.theme.DreamCard
import com.example.ui.theme.DreamCardBorder
import com.example.ui.theme.DreamCyan
import com.example.ui.theme.DreamDark
import com.example.ui.theme.DreamEmerald
import com.example.ui.theme.DreamGlow
import com.example.ui.theme.TextFaint
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun JournalView(journalRepo: LucidJournalRepository) {
    val dreams by journalRepo.records.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var wasLucid by remember { mutableStateOf(false) }

    val total = dreams.size
    val lucidCount = dreams.count { it.isLucid }
    val successRate = if (total > 0) Math.round((lucidCount.toFloat() / total.toFloat()) * 100) else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Analytics Stats 3-box row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    number = "$total",
                    label = "الأحلام المسجلة",
                    color = DreamGlow,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "$lucidCount",
                    label = "الأحلام الواضحة",
                    color = DreamEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "$successRate%",
                    label = "معدل النجاح",
                    color = DreamCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // New Dream Entry Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DreamCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = DreamGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توثيق حلم جديد",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DreamGlow
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("عنوان الحلم...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dream_title_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = DreamGlow,
                            unfocusedBorderColor = DreamCardBorder
                        )
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("ماذا رأيت في حلمك؟ هل استطعت التحكم بالطيران أو تغيير الأحداث؟...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dream_content_field"),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = DreamGlow,
                            unfocusedBorderColor = DreamCardBorder
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { wasLucid = !wasLucid }
                        ) {
                            Checkbox(
                                checked = wasLucid,
                                onCheckedChange = { wasLucid = it },
                                modifier = Modifier.testTag("was_lucid_checkbox"),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DreamGlow,
                                    uncheckedColor = TextFaint
                                )
                            )
                            Text(
                                text = "كان حلماً واعياً (Lucid) ✨",
                                fontSize = 12.sp,
                                color = if (wasLucid) DreamGlow else TextMuted,
                                fontWeight = if (wasLucid) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Button(
                            onClick = {
                                if (content.isNotBlank()) {
                                    journalRepo.saveDream(title, content, wasLucid)
                                    title = ""
                                    content = ""
                                    wasLucid = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DreamGlow),
                            modifier = Modifier.testTag("save_dream_btn")
                        ) {
                            Text("حفظ في السجل", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأحلام المسجلة سابقاً",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Text(
                    text = "${dreams.size} مدخلات",
                    fontSize = 11.sp,
                    color = TextFaint
                )
            }
        }

        // Dreams List
        items(dreams, key = { it.id }) { dream ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dream_card_${dream.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DreamCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dream.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (dream.isLucid) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DreamGlow.copy(alpha = 0.2f))
                                        .border(1.dp, DreamGlow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "حلم واعٍ ✨",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DreamGlow
                                    )
                                }
                            }
                            IconButton(
                                onClick = { journalRepo.deleteDream(dream.id) },
                                modifier = Modifier.size(28.dp).testTag("delete_dream_${dream.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = TextFaint,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dream.content,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dream.dateString,
                        fontSize = 10.sp,
                        color = TextFaint
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(number: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DreamCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextFaint
            )
        }
    }
}
