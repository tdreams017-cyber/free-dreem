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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.engine.ScenarioType
import com.example.ui.theme.DreamAccent
import com.example.ui.theme.DreamCard
import com.example.ui.theme.DreamCardBorder
import com.example.ui.theme.DreamCyan
import com.example.ui.theme.DreamDark
import com.example.ui.theme.DreamGlow
import com.example.ui.theme.DreamRose
import com.example.ui.theme.TextFaint
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun ScenarioView(audioEngine: LucidAudioEngine) {
    val selectedScenario by audioEngine.selectedScenario.collectAsState()
    val customPrompt by audioEngine.customPrompt.collectAsState()
    val customFreqHz by audioEngine.customFreqHz.collectAsState()
    val isPlayingPreview by audioEngine.isPlayingPreview.collectAsState()

    var showMildModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Scenario Card
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
                // Header & Active Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "تصميم السيناريوهات والإشارات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DreamCyan
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DreamGlow.copy(alpha = 0.2f))
                            .border(1.dp, DreamGlow.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedScenario.titleAr} | ${selectedScenario.beatFreq.toInt()}Hz",
                            fontSize = 10.sp,
                            color = DreamGlow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "اختر سيناريو الحلم الذي تريد تجربته الليلة. يربط التطبيق تلقائياً بين كل حلم وإشارة صوتية فريدة لتحفيز الدماغ على تذكره فور الاستماع إليها أثناء النوم.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 18.sp
                )

                // Grid 2x2 of Scenarios
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScenarioSelectButton(
                            scenario = ScenarioType.FLIGHT,
                            isSelected = selectedScenario == ScenarioType.FLIGHT,
                            accentColor = DreamAccent,
                            icon = Icons.Default.Flight,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectScenario(ScenarioType.FLIGHT) }
                        )

                        ScenarioSelectButton(
                            scenario = ScenarioType.FANTASY,
                            isSelected = selectedScenario == ScenarioType.FANTASY,
                            accentColor = DreamCyan,
                            icon = Icons.Default.Park,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectScenario(ScenarioType.FANTASY) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScenarioSelectButton(
                            scenario = ScenarioType.SPACE,
                            isSelected = selectedScenario == ScenarioType.SPACE,
                            accentColor = DreamRose,
                            icon = Icons.Default.RocketLaunch,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectScenario(ScenarioType.SPACE) }
                        )

                        ScenarioSelectButton(
                            scenario = ScenarioType.CUSTOM,
                            isSelected = selectedScenario == ScenarioType.CUSTOM,
                            accentColor = DreamGlow,
                            icon = Icons.Default.Tune,
                            modifier = Modifier.weight(1f),
                            onClick = { audioEngine.selectScenario(ScenarioType.CUSTOM) }
                        )
                    }
                }

                // If Custom Scenario is Selected
                if (selectedScenario == ScenarioType.CUSTOM) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DreamDark)
                            .border(1.dp, DreamCardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "اكتب سيناريو الحلم الخاص بك بدقة:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )

                        OutlinedTextField(
                            value = customPrompt,
                            onValueChange = { audioEngine.setCustomPrompt(it) },
                            placeholder = { Text("مثال: أريد دخول حلم في مدينة مستقبلية ولقاء شخصيات محددة...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
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
                            Text(
                                text = "تردد الوعي المخصص: ${customFreqHz}Hz",
                                style = MaterialTheme.typography.labelSmall,
                                color = DreamCyan
                            )
                        }

                        Slider(
                            value = customFreqHz.toFloat(),
                            onValueChange = { audioEngine.setCustomFreq(it.toInt()) },
                            valueRange = 20f..50f,
                            colors = SliderDefaults.colors(
                                thumbColor = DreamCyan,
                                activeTrackColor = DreamGlow,
                                inactiveTrackColor = DreamCardBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Test Audio Signal Button
                Button(
                    onClick = { audioEngine.playActiveCue() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("test_scenario_signal_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DreamGlow.copy(alpha = 0.2f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DreamGlow.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = DreamGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlayingPreview) "جاري تشغيل الإشارة الصوتية..." else "اختبر الإشارة الصوتية الخاصة بهذا الحلم الآن",
                        color = DreamGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // MILD Induction Button
                Button(
                    onClick = { showMildModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_mild_incubation_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بدء جلسة الاقتران والترسيخ قبل النوم (MILD)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // MILD Incubation Modal
    if (showMildModal) {
        AlertDialog(
            onDismissRequest = { showMildModal = false },
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
                            .background(DreamAccent.copy(alpha = 0.2f))
                            .border(1.dp, DreamAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = DreamAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ترسيخ الحلم (Incubation)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "خذ نفساً عميقاً، وأغلق عينيك. كرر في سرك 3 مرات بتركيز وهدوء:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DreamDark)
                            .border(1.dp, DreamGlow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\"أنا واعي وسأتذكر سيناريو الحلم عند سماع الإشارة\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = DreamGlow,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMildModal = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamAccent)
                ) {
                    Text("تم الترسيخ بنجاح - سأنم الآن 🌙", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ScenarioSelectButton(
    scenario: ScenarioType,
    isSelected: Boolean,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.15f) else DreamDark)
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor else DreamCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("scenario_card_${scenario.id}")
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = scenario.titleAr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) accentColor else TextLight
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = scenario.descAr,
                fontSize = 10.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )
        }
    }
}
