package com.example.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
fun SignalsView(audioEngine: LucidAudioEngine) {
    val isPlayingPreview by audioEngine.isPlayingPreview.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = DreamCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "محرك الإشارات الصوتية المستهدفة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DreamCyan
                    )
                }

                Text(
                    text = "يعتمد هذا النظام على ربط \"بصمة صوتية\" فريدة بكل سيناريو حلم. عندما يعزف التطبيق هذه الإشارة أثناء النوم، يتعرف عليها عقلك الباطن فوراً ويستدعي السيناريو المقترن بها.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 18.sp
                )

                // Signal Blueprint Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SignalBlueprintCard(
                        title = "إشارة حلم الطيران (Flight Signal)",
                        subtitle = "تردد متصاعد (40Hz Sweep) يعطي شعور الارتفاع",
                        accentColor = DreamAccent,
                        icon = Icons.Default.Flight,
                        onPlay = { audioEngine.playSignalPattern(ScenarioType.FLIGHT) }
                    )

                    SignalBlueprintCard(
                        title = "إشارة حلم الفانتزي (Nature Signal)",
                        subtitle = "ذبذبات ناعمة (38Hz Waves) استرخاء واستكشاف",
                        accentColor = DreamCyan,
                        icon = Icons.Default.Park,
                        onPlay = { audioEngine.playSignalPattern(ScenarioType.FANTASY) }
                    )

                    SignalBlueprintCard(
                        title = "إشارة الفضاء والمغامرات (Space Signal)",
                        subtitle = "نبضات ثلاثية الأبعاد متناوبة بين الأذنين (42Hz)",
                        accentColor = DreamRose,
                        icon = Icons.Default.RocketLaunch,
                        onPlay = { audioEngine.playSignalPattern(ScenarioType.SPACE) }
                    )
                }
            }
        }
    }
}

@Composable
fun SignalBlueprintCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onPlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DreamDark)
            .border(1.dp, DreamCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onPlay,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
        ) {
            Text(
                text = "سماع الإشارة",
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
