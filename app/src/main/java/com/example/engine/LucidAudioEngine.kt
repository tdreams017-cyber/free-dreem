package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class ScenarioType(
    val id: String,
    val titleAr: String,
    val descAr: String,
    val signalNameAr: String,
    val baseFreq: Double,
    val beatFreq: Double,
    val pattern: String,
    val iconName: String
) {
    FLIGHT(
        id = "flight",
        titleAr = "حلم الطيران",
        descAr = "إشارة: Gamma Rising Sweeps (40Hz)",
        signalNameAr = "تردد متصاعد (40Hz Sweep) يعطي إحساس الارتفاع",
        baseFreq = 220.0,
        beatFreq = 40.0,
        pattern = "rising",
        iconName = "plane"
    ),
    FANTASY(
        id = "fantasy",
        titleAr = "حلم الفانتزي والطبيعة",
        descAr = "إشارة: Theta Resonance (38Hz)",
        signalNameAr = "ذبذبات ناعمة (38Hz Waves) للاسترخاء والاستكشاف",
        baseFreq = 220.0,
        beatFreq = 38.0,
        pattern = "theta_soft",
        iconName = "nature"
    ),
    SPACE(
        id = "space",
        titleAr = "السفر والمجرة",
        descAr = "إشارة: 3D Alternating Pulse (42Hz)",
        signalNameAr = "نبضات ثلاثية الأبعاد متناوبة بين الأذنين (42Hz)",
        baseFreq = 220.0,
        beatFreq = 42.0,
        pattern = "binaural_3d",
        iconName = "space"
    ),
    CUSTOM(
        id = "custom",
        titleAr = "حلم مخصص ورادار",
        descAr = "إشارة: برمجة تذبذب يدوية",
        signalNameAr = "تردد وتذبذب مخصص حسب رغبتك",
        baseFreq = 220.0,
        beatFreq = 40.0,
        pattern = "rising",
        iconName = "custom"
    )
}

enum class WavePreset(
    val id: String,
    val nameAr: String,
    val descAr: String,
    val beatHz: Double
) {
    GAMMA("gamma", "موجات جاما (40Hz)", "تحفيز الوعي القشري الإدراكي", 40.0),
    THETA("theta", "موجات سيتا (6Hz)", "للاسترخاء والغوص ببطء", 6.0),
    ALPHA("alpha", "موجات ألفا (10Hz)", "صفاء ذهني وتصفية أفكار", 10.0),
    PULSED("pulsed", "3D Binaural Pulse (15Hz)", "نبضات ثلاثية الأبعاد متناوبة", 15.0)
}

class LucidAudioEngine(private val scope: CoroutineScope) {

    private val sampleRate = 44100
    private val bufferSize = 2048

    // Selected state
    private val _selectedScenario = MutableStateFlow(ScenarioType.FLIGHT)
    val selectedScenario: StateFlow<ScenarioType> = _selectedScenario.asStateFlow()

    private val _customPrompt = MutableStateFlow("")
    val customPrompt: StateFlow<String> = _customPrompt.asStateFlow()

    private val _customFreqHz = MutableStateFlow(40)
    val customFreqHz: StateFlow<Int> = _customFreqHz.asStateFlow()

    private val _customPattern = MutableStateFlow("rising")
    val customPattern: StateFlow<String> = _customPattern.asStateFlow()

    private val _selectedPreset = MutableStateFlow(WavePreset.GAMMA)
    val selectedPreset: StateFlow<WavePreset> = _selectedPreset.asStateFlow()

    private val _cueVolume = MutableStateFlow(0.30f)
    val cueVolume: StateFlow<Float> = _cueVolume.asStateFlow()

    private val _remIntervalMinutes = MutableStateFlow(15)
    val remIntervalMinutes: StateFlow<Int> = _remIntervalMinutes.asStateFlow()

    private val _wbtbEnabled = MutableStateFlow(true)
    val wbtbEnabled: StateFlow<Boolean> = _wbtbEnabled.asStateFlow()

    // Session state
    private val _isSessionRunning = MutableStateFlow(false)
    val isSessionRunning: StateFlow<Boolean> = _isSessionRunning.asStateFlow()

    private val _initialDelayHours = MutableStateFlow(4.5f)
    val initialDelayHours: StateFlow<Float> = _initialDelayHours.asStateFlow()

    private val _remainingSeconds = MutableStateFlow((4.5 * 3600).toLong())
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _completedCycles = MutableStateFlow(0)
    val completedCycles: StateFlow<Int> = _completedCycles.asStateFlow()

    private val _sessionStatusText = MutableStateFlow("وقت الانتظار لولادة مرحلة الـ REM الأولى")
    val sessionStatusText: StateFlow<String> = _sessionStatusText.asStateFlow()

    private val _isPlayingPreview = MutableStateFlow(false)
    val isPlayingPreview: StateFlow<Boolean> = _isPlayingPreview.asStateFlow()

    private var sessionJob: Job? = null
    private var previewJob: Job? = null
    private var previewTrack: AudioTrack? = null

    fun selectScenario(scenario: ScenarioType) {
        _selectedScenario.value = scenario
    }

    fun setCustomPrompt(prompt: String) {
        _customPrompt.value = prompt
    }

    fun setCustomFreq(freq: Int) {
        _customFreqHz.value = freq.coerceIn(20, 50)
    }

    fun setCustomPattern(pattern: String) {
        _customPattern.value = pattern
    }

    fun selectPreset(preset: WavePreset) {
        _selectedPreset.value = preset
    }

    fun setCueVolume(volume: Float) {
        _cueVolume.value = volume.coerceIn(0.05f, 1.0f)
    }

    fun setRemInterval(minutes: Int) {
        _remIntervalMinutes.value = minutes.coerceIn(5, 30)
    }

    fun toggleWbtb() {
        _wbtbEnabled.value = !_wbtbEnabled.value
    }

    fun setInitialDelay(hours: Float) {
        if (!_isSessionRunning.value) {
            _initialDelayHours.value = hours
            _remainingSeconds.value = (hours * 3600).toLong()
        }
    }

    fun toggleSleepSession() {
        if (_isSessionRunning.value) {
            stopSleepSession()
        } else {
            startSleepSession()
        }
    }

    private fun startSleepSession() {
        _isSessionRunning.value = true
        _completedCycles.value = 0
        _remainingSeconds.value = (_initialDelayHours.value * 3600).toLong()
        _sessionStatusText.value = "الخلفية نشطة • بانتظار مرحلة حركة العين السريعة (REM)"

        sessionJob?.cancel()
        sessionJob = scope.launch(Dispatchers.Default) {
            // Count down until initial REM window
            while (isActive && _remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value = _remainingSeconds.value - 1
            }

            if (!isActive) return@launch

            _completedCycles.value = 1
            _sessionStatusText.value = "دورة الـ REM نشطة الآن • إرسال الإشارات الصوتية الهادئة"

            // If WBTB enabled, trigger gentle chime alert
            if (_wbtbEnabled.value) {
                playSignalAudio(durationSeconds = 6, frequencyHz = 440.0, beatHz = 40.0, pattern = "rising")
            }

            // Periodic REM cues
            while (isActive) {
                playActiveCue()
                _completedCycles.value = _completedCycles.value + 1
                val intervalMs = _remIntervalMinutes.value * 60 * 1000L
                delay(intervalMs)
            }
        }
    }

    fun stopSleepSession() {
        _isSessionRunning.value = false
        sessionJob?.cancel()
        sessionJob = null
        _remainingSeconds.value = (_initialDelayHours.value * 3600).toLong()
        _sessionStatusText.value = "جلسة النوم متوقفة"
    }

    fun playActiveCue() {
        val scenario = _selectedScenario.value
        val beat = if (scenario == ScenarioType.CUSTOM) _customFreqHz.value.toDouble() else scenario.beatFreq
        val pattern = if (scenario == ScenarioType.CUSTOM) _customPattern.value else scenario.pattern
        playSignalAudio(durationSeconds = 12, frequencyHz = scenario.baseFreq, beatHz = beat, pattern = pattern)
    }

    fun playPresetCue(preset: WavePreset) {
        val pattern = if (preset == WavePreset.PULSED) "binaural_3d" else "theta_soft"
        playSignalAudio(durationSeconds = 12, frequencyHz = 210.0, beatHz = preset.beatHz, pattern = pattern)
    }

    fun playSignalPattern(type: ScenarioType) {
        val beat = if (type == ScenarioType.CUSTOM) _customFreqHz.value.toDouble() else type.beatFreq
        val pattern = if (type == ScenarioType.CUSTOM) _customPattern.value else type.pattern
        playSignalAudio(durationSeconds = 12, frequencyHz = type.baseFreq, beatHz = beat, pattern = pattern)
    }

    private fun playSignalAudio(
        durationSeconds: Int,
        frequencyHz: Double,
        beatHz: Double,
        pattern: String
    ) {
        previewJob?.cancel()
        try {
            previewTrack?.stop()
            previewTrack?.release()
        } catch (e: Exception) {
            // ignore
        }

        _isPlayingPreview.value = true

        previewJob = scope.launch(Dispatchers.Default) {
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(bufferSize * 4)

                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val format = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()

                previewTrack = AudioTrack.Builder()
                    .setAudioAttributes(attributes)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                previewTrack?.play()

                val totalSamples = durationSeconds * sampleRate
                val stereoBuffer = ShortArray(bufferSize * 2) // 2 channels
                val twoPi = 2.0 * Math.PI
                var currentSample = 0
                val vol = _cueVolume.value

                while (isActive && currentSample < totalSamples) {
                    val batchSize = minOf(bufferSize, totalSamples - currentSample)
                    for (i in 0 until batchSize) {
                        val t = (currentSample + i).toDouble() / sampleRate
                        val totalDuration = durationSeconds.toDouble()

                        // Envelope: 3s attack, sustain, 3s release
                        val envelope = when {
                            t < 3.0 -> (t / 3.0) * vol
                            t > totalDuration - 3.0 -> ((totalDuration - t) / 3.0).coerceAtLeast(0.0) * vol
                            else -> vol.toDouble()
                        }

                        // Frequency modulation based on pattern
                        var currentLeftFreq = frequencyHz
                        var currentRightFreq = frequencyHz + beatHz

                        if (pattern == "rising") {
                            val sweep = (t / totalDuration) * 30.0
                            currentLeftFreq += sweep
                            currentRightFreq += sweep
                        }

                        val leftPhase = (twoPi * currentLeftFreq * t) % twoPi
                        val rightPhase = (twoPi * currentRightFreq * t) % twoPi

                        var leftSample = sin(leftPhase) * envelope
                        var rightSample = sin(rightPhase) * envelope

                        if (pattern == "binaural_3d") {
                            // Pan back and forth across 3D soundfield
                            val pan = sin(twoPi * 0.25 * t) // slow alternating pan
                            val leftGain = (1.0 - pan) * 0.5
                            val rightGain = (1.0 + pan) * 0.5
                            leftSample *= leftGain
                            rightSample *= rightGain
                        }

                        stereoBuffer[i * 2] = (leftSample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                        stereoBuffer[i * 2 + 1] = (rightSample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                    }

                    previewTrack?.write(stereoBuffer, 0, batchSize * 2)
                    currentSample += batchSize
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPlayingPreview.value = false
                try {
                    previewTrack?.stop()
                    previewTrack?.release()
                } catch (e: Exception) {
                    // ignore
                }
                previewTrack = null
            }
        }
    }
}
