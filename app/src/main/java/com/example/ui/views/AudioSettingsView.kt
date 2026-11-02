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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LucidAudioEngine
import com.example.engine.WavePreset
import com.example.ui.theme.DreamAccent
import com.example.ui.theme.DreamCard
import com.example.ui.theme.DreamCardBorder
import com.example.ui.theme.DreamCyan
import com.example.ui.theme.DreamDark
import com.example.ui.theme.DreamEmerald
import com.example.ui.theme.DreamGlow
import com.example.ui.theme.DreamRose
import com.example.ui.theme.TextFaint
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun AudioSettingsView(audioEngine: LucidAudioEngine) {
    val selectedPreset by audioEngine.selectedPreset.collectAsState()
    val cueVolume by audioEngine.cueVolume.collectAsState()
    val remInterval by audioEngine.remIntervalMinutes.collectAsState()
    val isPlayingPreview by audioEngine.isPlayingPreview.collectAsState()

    var showRealityModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Binaural Presets Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DreamCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = DreamAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تردد الموجات الصوتية والتحفيز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DreamAccent
                    )
                }

                Text(
                    text = "نمط النغمة المستهدفة لليلة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                // 2x2 Presets
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetCard(
                            preset = WavePreset.GAMMA,
                            isSelected = selectedPreset == WavePreset.GAMMA,
                            accentColor = DreamGlow,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectPreset(WavePreset.GAMMA) }
                        )
                        PresetCard(
                            preset = WavePreset.THETA,
                            isSelected = selectedPreset == WavePreset.THETA,
                            accentColor = DreamCyan,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectPreset(WavePreset.THETA) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetCard(
                            preset = WavePreset.ALPHA,
                            isSelected = selectedPreset == WavePreset.ALPHA,
                            accentColor = DreamEmerald,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectPreset(WavePreset.ALPHA) }
                        )
                        PresetCard(
                            preset = WavePreset.PULSED,
                            isSelected = selectedPreset == WavePreset.PULSED,
                            accentColor = DreamRose,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectPreset(WavePreset.PULSED) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Cue Volume Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "درجة الصوت (Cue Volume)", fontSize = 12.sp, color = TextLight)
                        Text(
                            text = "${(cueVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DreamGlow
                        )
                    }

                    Slider(
                        value = cueVolume,
                        onValueChange = { audioEngine.setCueVolume(it) },
                        valueRange = 0.05f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = DreamGlow,
                            activeTrackColor = DreamAccent,
                            inactiveTrackColor = DreamDark
                        )
                    )
                }

                // REM Interval Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "فاصل تكرار الإشارة في الـ REM", fontSize = 12.sp, color = TextLight)
                        Text(
                            text = "$remInterval دقيقة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DreamCyan
                        )
                    }

                    Slider(
                        value = remInterval.toFloat(),
                        onValueChange = { audioEngine.setRemInterval(it.toInt()) },
                        valueRange = 5f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = DreamCyan,
                            activeTrackColor = DreamCyan,
                            inactiveTrackColor = DreamDark
                        )
                    )
                }

                // Test Audio Signal
                Button(
                    onClick = { audioEngine.playPresetCue(selectedPreset) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("test_binaural_preset_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamAccent.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DreamAccent.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = DreamAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlayingPreview) "جاري الاستماع للإشارة..." else "اختبر الإشارة الصوتية الآن",
                        color = DreamAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Reality Check Reminders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DreamCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = DreamCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اختبار الواقع السريع (Reality Check)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DreamCyan
                    )
                }

                Text(
                    text = "تعويد عقلك على فحص الواقع خلال النهار يساعدك على تكرار الفحص تلقائياً داخل الحلم وإدراك أنك تحلم.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 18.sp
                )

                Button(
                    onClick = { showRealityModal = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
                ) {
                    Text(
                        text = "تنفيذ اختبار واقع سريع الآن",
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Reality Modal
    if (showRealityModal) {
        AlertDialog(
            onDismissRequest = { showRealityModal = false },
            containerColor = DreamCard,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(DreamGlow.copy(alpha = 0.2f))
                            .border(1.dp, DreamGlow.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = DreamGlow,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "اختبار الواقع السريع!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "انظر إلى يديك الآن أو حاول دفع إبهامك برفق عبر كف يدك الأخرى.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "هل أنت واعي وفي الواقع أم أنك تحلم؟",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = DreamGlow,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRealityModal = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamGlow)
                ) {
                    Text("أنا واعي ومستيقظ تماماً 👍", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun PresetCard(
    preset: WavePreset,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else DreamDark)
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor else DreamCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("preset_card_${preset.id}")
    ) {
        Column {
            Text(
                text = preset.nameAr,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = preset.descAr,
                fontSize = 10.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )
        }
    }
}
