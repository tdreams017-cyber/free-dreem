package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LucidAudioEngine
import com.example.journal.LucidJournalRepository
import com.example.ui.theme.DreamAccent
import com.example.ui.theme.DreamCard
import com.example.ui.theme.DreamCardBorder
import com.example.ui.theme.DreamCyan
import com.example.ui.theme.DreamDark
import com.example.ui.theme.DreamEmerald
import com.example.ui.theme.DreamGlow
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextFaint
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.views.AudioSettingsView
import com.example.ui.views.JournalView
import com.example.ui.views.ScenarioView
import com.example.ui.views.SignalsView
import com.example.ui.views.SleepView

class MainActivity : ComponentActivity() {

    private var audioEngine: LucidAudioEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                val engine = remember {
                    LucidAudioEngine(coroutineScope).also { audioEngine = it }
                }
                val journalRepo = remember {
                    LucidJournalRepository(applicationContext)
                }

                LucidAppScreen(audioEngine = engine, journalRepo = journalRepo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine?.stopSleepSession()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LucidAppScreen(
    audioEngine: LucidAudioEngine,
    journalRepo: LucidJournalRepository
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isSessionRunning by audioEngine.isSessionRunning.collectAsState()

    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DreamDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DreamAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Lucid AI Pro",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DreamGlow.copy(alpha = 0.2f))
                                            .border(1.dp, DreamGlow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "v2.5",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DreamGlow
                                        )
                                    }
                                }
                                Text(
                                    text = "مُنسّق ومُصمم الأحلام الواضحة الذكي",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Background status pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DreamCard)
                                .border(1.dp, DreamCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSessionRunning) DreamEmerald else TextFaint)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSessionRunning) "الخلفية نشطة" else "الخلفية متوقفة",
                                    fontSize = 10.sp,
                                    color = if (isSessionRunning) DreamEmerald else TextMuted
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DreamDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DreamCard,
                modifier = Modifier.testTag("lucid_nav_bar")
            ) {
                // Tab 0: Sleep
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(Icons.Default.Hotel, contentDescription = "النوم", modifier = Modifier.size(20.dp))
                    },
                    label = { Text("النوم", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_sleep"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DreamGlow,
                        indicatorColor = DreamAccent,
                        unselectedIconColor = TextFaint,
                        unselectedTextColor = TextFaint
                    )
                )

                // Tab 1: Scenario
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(Icons.Default.Explore, contentDescription = "السيناريو", modifier = Modifier.size(20.dp))
                    },
                    label = { Text("السيناريو", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_scenario"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DreamGlow,
                        indicatorColor = DreamAccent,
                        unselectedIconColor = TextFaint,
                        unselectedTextColor = TextFaint
                    )
                )

                // Tab 2: Signals
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(Icons.Default.Sensors, contentDescription = "الإشارات", modifier = Modifier.size(20.dp))
                    },
                    label = { Text("الإشارات", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_signals"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DreamGlow,
                        indicatorColor = DreamAccent,
                        unselectedIconColor = TextFaint,
                        unselectedTextColor = TextFaint
                    )
                )

                // Tab 3: Waves / Audio
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(Icons.Default.Tune, contentDescription = "الموجات", modifier = Modifier.size(20.dp))
                    },
                    label = { Text("الموجات", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_audio"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DreamGlow,
                        indicatorColor = DreamAccent,
                        unselectedIconColor = TextFaint,
                        unselectedTextColor = TextFaint
                    )
                )

                // Tab 4: Journal
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(Icons.Default.Bookmark, contentDescription = "السجل", modifier = Modifier.size(20.dp))
                    },
                    label = { Text("السجل", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_journal"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DreamGlow,
                        indicatorColor = DreamAccent,
                        unselectedIconColor = TextFaint,
                        unselectedTextColor = TextFaint
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DreamDark)
        ) {
            when (selectedTab) {
                0 -> SleepView(audioEngine = audioEngine)
                1 -> ScenarioView(audioEngine = audioEngine)
                2 -> SignalsView(audioEngine = audioEngine)
                3 -> AudioSettingsView(audioEngine = audioEngine)
                4 -> JournalView(journalRepo = journalRepo)
            }
        }
    }
}
