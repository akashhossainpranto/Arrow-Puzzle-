package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.game.data.GamePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural low-latency sound effect manager.
 * Synthesizes crisp game audio (tap, slide, error thud, victory chime) into PCM WAV files
 * and plays them via Android SoundPool.
 */
class SoundManager(
    private val context: Context,
    private val preferences: GamePreferences
) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
        )
        .build()

    private var soundTapId: Int = 0
    private var soundSlideId: Int = 0
    private var soundBlockedId: Int = 0
    private var soundWinId: Int = 0
    private var soundHeartId: Int = 0
    private var soundHintId: Int = 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                generateAndLoadSounds()
            } catch (e: Exception) {
                Log.e("SoundManager", "Failed to generate sounds", e)
            }
        }
    }

    private fun generateAndLoadSounds() {
        val cacheDir = context.cacheDir
        soundTapId = loadSynthesizedWav(cacheDir, "snd_tap.wav", generateTap())
        soundSlideId = loadSynthesizedWav(cacheDir, "snd_slide.wav", generateSlide())
        soundBlockedId = loadSynthesizedWav(cacheDir, "snd_blocked.wav", generateBlocked())
        soundWinId = loadSynthesizedWav(cacheDir, "snd_win.wav", generateWin())
        soundHeartId = loadSynthesizedWav(cacheDir, "snd_heart.wav", generateHeart())
        soundHintId = loadSynthesizedWav(cacheDir, "snd_hint.wav", generateHint())
    }

    private fun loadSynthesizedWav(dir: File, filename: String, pcmData: ByteArray): Int {
        val file = File(dir, filename)
        FileOutputStream(file).use { fos ->
            val wavHeader = createWavHeader(pcmData.size, 22050, 1, 16)
            fos.write(wavHeader)
            fos.write(pcmData)
        }
        return soundPool.load(file.absolutePath, 1)
    }

    fun playTap() = play(soundTapId, 0.7f, 1.0f)
    fun playSlide() = play(soundSlideId, 0.8f, 1.0f)
    fun playBlocked() = play(soundBlockedId, 0.9f, 0.95f)
    fun playWin() = play(soundWinId, 1.0f, 1.0f)
    fun playHeartLost() = play(soundHeartId, 0.8f, 1.0f)
    fun playHint() = play(soundHintId, 0.8f, 1.05f)

    private fun play(soundId: Int, volume: Float, rate: Float) {
        if (!preferences.isSoundEnabled || soundId == 0) return
        soundPool.play(soundId, volume, volume, 1, 0, rate)
    }

    fun release() {
        soundPool.release()
    }

    // --- Audio Synthesis Generators (22050Hz, 16-bit mono) ---

    private fun generateTap(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.06
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 750.0 - (t / durationSec) * 450.0 // Pitch drop from 750 to 300Hz
            val envelope = exp(-t * 70.0) // Fast snappy decay
            val sample = (sin(2.0 * PI * freq * t) * envelope * 24000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun generateSlide(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.12
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / durationSec
            val freq = 550.0 + progress * 850.0 // Snappy rising zip
            val envelope = (1.0 - progress * progress) * (1.0 - exp(-t * 90.0))
            val sample = (sin(2.0 * PI * freq * t) * envelope * 24000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun generateBlocked(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.16
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 135.0
            val harmonic = 270.0
            val envelope = exp(-t * 28.0)
            val wave = sin(2.0 * PI * freq * t) * 0.7 + sin(2.0 * PI * harmonic * t) * 0.3
            val sample = (wave * envelope * 26000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun generateWin(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.7
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        // Arpeggiated C-Major chords: C5, E5, G5, C6
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val noteOffset = 0.10 // 100ms per note

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var waveSum = 0.0

            for (n in notes.indices) {
                val startTime = n * noteOffset
                if (t >= startTime) {
                    val noteT = t - startTime
                    val envelope = exp(-noteT * 6.0)
                    waveSum += sin(2.0 * PI * notes[n] * noteT) * envelope * 0.35
                }
            }
            val sample = (waveSum * 28000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun generateHeart(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.15
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / durationSec
            val freq = 420.0 - progress * 220.0 // Descending blip
            val envelope = exp(-t * 22.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 22000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun generateHint(): ByteArray {
        val sampleRate = 22050
        val durationSec = 0.35
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 987.77 // B5 bright chime
            val tremolo = 1.0 + 0.15 * sin(2.0 * PI * 18.0 * t)
            val envelope = exp(-t * 10.0)
            val sample = (sin(2.0 * PI * freq * t) * tremolo * envelope * 24000.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(sample.toShort())
        }
        return buffer.array()
    }

    private fun createWavHeader(dataSize: Int, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val totalSize = dataSize + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray())
        header.putInt(totalSize)
        header.put("WAVE".toByteArray())
        header.put("fmt ".toByteArray())
        header.putInt(16) // Subchunk1Size for PCM
        header.putShort(1.toShort()) // AudioFormat 1 = PCM
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(blockAlign.toShort())
        header.putShort(bitsPerSample.toShort())
        header.put("data".toByteArray())
        header.putInt(dataSize)
        return header.array()
    }
}
