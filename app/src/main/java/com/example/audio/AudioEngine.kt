package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

data class PlaybackProgress(
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val waveformAmplitudes: List<Float> = emptyList()
)

class AudioEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val tag = "AudioEngine"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var currentTrack: Track? = null

    private val _playbackProgress = MutableStateFlow(PlaybackProgress())
    val playbackProgress: StateFlow<PlaybackProgress> = _playbackProgress.asStateFlow()

    private var onTrackCompletedListener: (() -> Unit)? = null
    private var playbackSpeed: Float = 1.0f

    fun setOnTrackCompletedListener(listener: () -> Unit) {
        onTrackCompletedListener = listener
    }

    fun playTrack(track: Track, localFilePath: String? = null, startPositionMs: Long = 0L) {
        currentTrack = track
        stopProgressTracker()

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                _playbackProgress.value = _playbackProgress.value.copy(
                    isBuffering = true,
                    durationMs = track.durationMs,
                    currentPositionMs = startPositionMs
                )

                if (localFilePath != null && File(localFilePath).exists()) {
                    setDataSource(localFilePath)
                } else {
                    setDataSource(track.audioUrl)
                }

                setOnPreparedListener { mp ->
                    try {
                        _playbackProgress.value = _playbackProgress.value.copy(
                            isBuffering = false,
                            durationMs = mp.duration.toLong().coerceAtLeast(track.durationMs)
                        )
                        if (startPositionMs > 0L) {
                            mp.seekTo(startPositionMs.toInt())
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            mp.playbackParams = mp.playbackParams.setSpeed(playbackSpeed)
                        }
                        mp.start()
                        _playbackProgress.value = _playbackProgress.value.copy(isPlaying = true)
                        startProgressTracker()
                    } catch (e: Exception) {
                        Log.e(tag, "Error starting after prepared", e)
                    }
                }

                setOnCompletionListener {
                    _playbackProgress.value = _playbackProgress.value.copy(isPlaying = false)
                    stopProgressTracker()
                    onTrackCompletedListener?.invoke()
                }

                setOnErrorListener { _, what, extra ->
                    Log.w(tag, "MediaPlayer error: what=$what, extra=$extra")
                    _playbackProgress.value = _playbackProgress.value.copy(
                        isPlaying = false,
                        isBuffering = false
                    )
                    // Return true so onCompletion is not called automatically
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize playback for track ${track.title}", e)
            _playbackProgress.value = _playbackProgress.value.copy(
                isPlaying = false,
                isBuffering = false
            )
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let { mp ->
                if (!mp.isPlaying) {
                    mp.start()
                    _playbackProgress.value = _playbackProgress.value.copy(isPlaying = true)
                    startProgressTracker()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error resuming playback", e)
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.pause()
                    _playbackProgress.value = _playbackProgress.value.copy(isPlaying = false)
                    stopProgressTracker()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing playback", e)
        }
    }

    fun togglePlayPause() {
        if (_playbackProgress.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.let { mp ->
                mp.seekTo(positionMs.toInt())
                _playbackProgress.value = _playbackProgress.value.copy(currentPositionMs = positionMs)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error seeking", e)
        }
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mediaPlayer?.let { mp ->
                    mp.playbackParams = mp.playbackParams.setSpeed(speed)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting speed", e)
        }
    }

    fun setVolume(volume: Float) {
        try {
            mediaPlayer?.setVolume(volume, volume)
        } catch (e: Exception) {
            Log.e(tag, "Error setting volume", e)
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch(Dispatchers.Default) {
            val random = Random(42)
            while (isActive) {
                try {
                    val currentPos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    val duration = (mediaPlayer?.duration?.toLong() ?: 0L).coerceAtLeast(1L)
                    val isPlaying = mediaPlayer?.isPlaying ?: false

                    // Generate simulated dynamic 32-bar visualizer amplitudes
                    val amplitudes = List(32) { index ->
                        val base = 0.2f + 0.6f * kotlin.math.sin((currentPos / 200f) + index * 0.4f).toFloat().coerceIn(0f, 1f)
                        val noise = if (isPlaying) random.nextFloat() * 0.2f else 0.05f
                        (base + noise).coerceIn(0.1f, 1.0f)
                    }

                    _playbackProgress.value = _playbackProgress.value.copy(
                        currentPositionMs = currentPos,
                        durationMs = duration,
                        isPlaying = isPlaying,
                        waveformAmplitudes = amplitudes
                    )
                } catch (e: Exception) {
                    // Ignore transient exceptions during seek/buffering
                }
                delay(250)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(tag, "Error releasing player", e)
        }
        mediaPlayer = null
    }
}
