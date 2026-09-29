package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

/**
 * Manages game sound effects and haptics.
 * Uses a single pooled SoundPool with pre-synthesized PCM wav assets to prevent
 * AudioFlinger track exhaustion (error -12 / error -20).
 */
class SoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private var soundPool: SoundPool? = null
    private var bounceSoundId: Int = 0
    private var powerUpSoundId: Int = 0
    private var eliminationSoundId: Int = 0
    private var victorySoundId: Int = 0

    private var bounceLoaded = false
    private var powerUpLoaded = false
    private var eliminationLoaded = false
    private var victoryLoaded = false

    private var lastBounceSoundTime = 0L

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build().apply {
                    setOnLoadCompleteListener { _, sampleId, status ->
                        if (status == 0) {
                            when (sampleId) {
                                bounceSoundId -> bounceLoaded = true
                                powerUpSoundId -> powerUpLoaded = true
                                eliminationSoundId -> eliminationLoaded = true
                                victorySoundId -> victoryLoaded = true
                            }
                        }
                    }
                }

            scope.launch {
                prepareAudioAssets()
            }
        } catch (_: Throwable) {
            // Audio hardware or AudioFlinger unavailable
            soundPool = null
        }
    }

    private fun prepareAudioAssets() {
        try {
            val cacheDir = context.cacheDir ?: return
            val sampleRate = 22050

            // 1. Bounce tone (45ms decayed sine wave)
            val bounceSamples = generateToneSamples(320f, 45, sampleRate, 0.45f)
            val bounceFile = File(cacheDir, "sfx_bounce.wav")
            writeWavFile(bounceFile, bounceSamples, sampleRate)
            soundPool?.let { bounceSoundId = it.load(bounceFile.absolutePath, 1) }

            // 2. Power-Up chime (160ms rising arpeggio C5 -> E5 -> G5)
            val powerUpSamples = generateArpeggioSamples(listOf(523.25f, 659.25f, 783.99f), 160, sampleRate, 0.5f)
            val powerUpFile = File(cacheDir, "sfx_powerup.wav")
            writeWavFile(powerUpFile, powerUpSamples, sampleRate)
            soundPool?.let { powerUpSoundId = it.load(powerUpFile.absolutePath, 1) }

            // 3. Elimination thud (110ms low sweep with impact noise)
            val elimSamples = generateThudSamples(110, sampleRate, 0.6f)
            val elimFile = File(cacheDir, "sfx_elim.wav")
            writeWavFile(elimFile, elimSamples, sampleRate)
            soundPool?.let { eliminationSoundId = it.load(elimFile.absolutePath, 1) }

            // 4. Victory fanfare (260ms chord)
            val victorySamples = generateChordSamples(listOf(523.25f, 659.25f, 783.99f, 1046.50f), 260, sampleRate, 0.55f)
            val victoryFile = File(cacheDir, "sfx_victory.wav")
            writeWavFile(victoryFile, victorySamples, sampleRate)
            soundPool?.let { victorySoundId = it.load(victoryFile.absolutePath, 1) }
        } catch (_: Throwable) {
            // Audio synthesis gracefully handled
        }
    }

    fun playBounce(speedRatio: Float = 0.5f) {
        val now = System.currentTimeMillis()
        // Rate-limit bounces to prevent audio spam during heavy multi-ball collisions
        if (now - lastBounceSoundTime < 65) return
        lastBounceSoundTime = now

        if (soundEnabled && bounceLoaded && soundPool != null) {
            try {
                val volume = (0.2f + (speedRatio.coerceIn(0f, 1f) * 0.45f))
                val pitch = (0.85f + (speedRatio.coerceIn(0f, 1f) * 0.35f))
                soundPool?.play(bounceSoundId, volume, volume, 1, 0, pitch)
            } catch (_: Throwable) {}
        }
        if (hapticsEnabled && speedRatio > 0.45f) {
            triggerVibration(12, (speedRatio * 140).toInt().coerceIn(30, 180))
        }
    }

    fun playPowerUp() {
        if (soundEnabled && powerUpLoaded && soundPool != null) {
            try {
                soundPool?.play(powerUpSoundId, 0.6f, 0.6f, 2, 0, 1.0f)
            } catch (_: Throwable) {}
        }
        if (hapticsEnabled) {
            triggerVibration(40, 180)
        }
    }

    fun playElimination() {
        if (soundEnabled && eliminationLoaded && soundPool != null) {
            try {
                soundPool?.play(eliminationSoundId, 0.7f, 0.7f, 2, 0, 1.0f)
            } catch (_: Throwable) {}
        }
        if (hapticsEnabled) {
            triggerVibration(70, 240)
        }
    }

    fun playVictory() {
        if (soundEnabled && victoryLoaded && soundPool != null) {
            try {
                soundPool?.play(victorySoundId, 0.8f, 0.8f, 3, 0, 1.0f)
            } catch (_: Throwable) {}
        }
        if (hapticsEnabled) {
            triggerVibration(120, 255)
        }
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (_: Throwable) {}
    }

    private fun triggerVibration(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Throwable) {}
    }

    // ================= AUDIO SYNTHESIS HELPERS =================

    private fun generateToneSamples(frequencyHz: Float, durationMs: Int, sampleRate: Int, volume: Float): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = 1.0 - (i.toDouble() / numSamples)
            val sample = sin(2.0 * PI * frequencyHz * t) * decay * volume
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateArpeggioSamples(notes: List<Float>, totalDurationMs: Int, sampleRate: Int, volume: Float): ShortArray {
        val totalSamples = (sampleRate * (totalDurationMs / 1000f)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(totalSamples)
        val noteSamples = totalSamples / notes.size.coerceAtLeast(1)

        for (i in 0 until totalSamples) {
            val noteIndex = (i / noteSamples).coerceIn(0, notes.size - 1)
            val freq = notes[noteIndex]
            val t = i.toDouble() / sampleRate
            val subIndex = i % noteSamples
            val decay = 1.0 - (subIndex.toDouble() / noteSamples * 0.7)
            val sample = sin(2.0 * PI * freq * t) * decay * volume
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateThudSamples(durationMs: Int, sampleRate: Int, volume: Float): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = (160.0 - (120.0 * (i.toDouble() / numSamples))).coerceAtLeast(35.0)
            val decay = 1.0 - (i.toDouble() / numSamples)
            val noise = (Math.random() * 2.0 - 1.0) * 0.2
            val sample = (sin(2.0 * PI * freq * t) * 0.8 + noise) * decay * volume
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateChordSamples(notes: List<Float>, durationMs: Int, sampleRate: Int, volume: Float): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = 1.0 - (i.toDouble() / numSamples)
            var sum = 0.0
            for (freq in notes) {
                sum += sin(2.0 * PI * freq * t)
            }
            val sample = (sum / notes.size) * decay * volume
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
        val byteRate = sampleRate * 2
        val totalDataLen = pcmData.size * 2 + 36
        val dataLen = pcmData.size * 2
        val header = ByteArray(44)

        // RIFF chunk descriptor
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()

        // "fmt " sub-chunk
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0 // Subchunk1Size = 16 for PCM
        header[20] = 1; header[21] = 0 // AudioFormat = 1 (PCM)
        header[22] = 1; header[23] = 0 // NumChannels = 1 (Mono)
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0 // BlockAlign = 2
        header[34] = 16; header[35] = 0 // BitsPerSample = 16

        // "data" sub-chunk
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (dataLen and 0xff).toByte()
        header[41] = ((dataLen shr 8) and 0xff).toByte()
        header[42] = ((dataLen shr 16) and 0xff).toByte()
        header[43] = ((dataLen shr 24) and 0xff).toByte()

        FileOutputStream(file).use { fos ->
            fos.write(header)
            val byteBuffer = ByteBuffer.allocate(pcmData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (s in pcmData) {
                byteBuffer.putShort(s)
            }
            fos.write(byteBuffer.array())
        }
    }
}
