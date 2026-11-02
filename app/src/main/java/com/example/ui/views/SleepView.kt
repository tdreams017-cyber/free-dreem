package com.example.ui.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LucidAudioEngine
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
import kotlin.math.sin

@Composable
fun SleepView(audioEngine: LucidAudioEngine) {
    val isRunning by audioEngine.isSessionRunning.collectAsState()
    val remainingSeconds by audioEngine.remainingSeconds.collectAsState()
    val selectedDelay by audioEngine.initialDelayHours.collectAsState()
    val wbtbEnabled by audioEngine.wbtbEnabled.collectAsState()
    val completedCycles by audioEngine.completedCycles.collectAsState()
    val statusText by audioEngine.sessionStatusText.collectAsState()

    // EEG Animation
    val infiniteTransition = rememberInfiniteTransition(label = "eeg_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    val totalDurationSeconds = (selectedDelay * 3600).toFloat()
    val progressFraction = if (totalDurationSeconds > 0) {
        ((totalDurationSeconds - remainingSeconds) / totalDurationSeconds).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Sleep Session Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sleep_session_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DreamCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // EEG Canvas Visualizer Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DreamDark)
                        .border(1.dp, DreamCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path()
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f
                        val points = 80
                        val stepX = w / points

                        path.moveTo(0f, midY)
                        for (i in 0..points) {
                            val x = i * stepX
                            val freqMod = if (isRunning) 4f else 2f
                            val amp = if (isRunning) (h * 0.35f) else (h * 0.12f)
                            val y = midY + sin((i * 0.3f) + wavePhase * freqMod) * amp
                            path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            color = if (isRunning) DreamGlow else DreamCyan.copy(alpha = 0.5f),
                            style = Stroke(width = 2.5f)
                        )
                    }

                    // Status Indicator
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) DreamEmerald else TextFaint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "محاكاة EEG: نشطة" else "محاكاة EEG: متوقفة",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isRunning) DreamGlow else TextFaint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Countdown Timer
                Text(
                    text = timeFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Circular Progress
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = DreamDark,
                        strokeWidth = 6.dp
                    )
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxSize(),
                        color = DreamGlow,
                        strokeWidth = 6.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bed,
                            contentDescription = null,
                            tint = DreamGlow,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "الدورة $completedCycles",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Delay Options (3h, 4.5h, 6h)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        3.0f to "3 ساعات",
                        4.5f to "4.5 ساعة (موصى)",
                        6.0f to "6 ساعات"
                    ).forEach { (delayHours, label) ->
                        val isSelected = selectedDelay == delayHours
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) DreamAccent.copy(alpha = 0.25f) else DreamDark
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) DreamAccent else DreamCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { audioEngine.setInitialDelay(delayHours) }
                                .padding(vertical = 10.dp)
                                .testTag("delay_option_${delayHours}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DreamAccent else TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // WBTB Toggle (Wake-Back-To-Bed)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DreamDark)
                        .border(1.dp, DreamCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = DreamAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نظام WBTB (منبه إيقاظ لطيف)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Switch(
                        checked = wbtbEnabled,
                        onCheckedChange = { audioEngine.toggleWbtb() },
                        modifier = Modifier.testTag("wbtb_toggle_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DreamGlow,
                            uncheckedThumbColor = TextFaint,
                            uncheckedTrackColor = DreamCardBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Main Start/Stop Action Button with Gradient
                Button(
                    onClick = { audioEngine.toggleSleepSession() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("toggle_sleep_session_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) DreamRose else DreamAccent
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "إيقاف جلسة النوم" else "بدء جلسة النوم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Placement & Background Operation Guidance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DreamCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DreamCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = DreamEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "وضع العمل بالشاشة المغلقة مفعل تلقائياً",
                        fontWeight = FontWeight.Bold,
                        color = DreamEmerald,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ضع هاتفك على الوسادة بجانب رأسك بعد الضغط على 'بدء جلسة النوم' واغلق الشاشة. سيستمر البرنامج في حساب أوقات دورات النوم وإرسال الإشارات الصوتية الهادئة فور الوصول لمرحلة الأحلام (REM).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
