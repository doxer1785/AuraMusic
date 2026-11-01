package com.example.audio

import android.content.Context
import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class OfflineCacheManager(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val cacheDir: File by lazy {
        File(context.filesDir, "aura_offline_music").apply {
            if (!exists()) mkdirs()
        }
    }

    fun isTrackDownloaded(trackId: String): Boolean {
        val file = File(cacheDir, "$trackId.mp3")
        return file.exists() && file.length() > 1024
    }

    fun getLocalFilePath(trackId: String): String? {
        val file = File(cacheDir, "$trackId.mp3")
        return if (file.exists() && file.length() > 1024) file.absolutePath else null
    }

    suspend fun downloadTrack(
        track: Track,
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val destinationFile = File(cacheDir, "${track.id}.mp3")
            val request = Request.Builder()
                .url(track.audioUrl)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(destinationFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    val progress = (totalBytesRead.toFloat() / contentLength).coerceIn(0f, 1f)
                    withContext(Dispatchers.Main) {
                        onProgress(progress)
                    }
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            withContext(Dispatchers.Main) {
                onProgress(1.0f)
            }

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeDownloadedTrack(trackId: String): Boolean {
        val file = File(cacheDir, "$trackId.mp3")
        return if (file.exists()) file.delete() else false
    }

    fun getTotalCacheSizeMb(): Float {
        val files = cacheDir.listFiles() ?: return 0f
        val bytes = files.sumOf { it.length() }
        return bytes / (1024f * 1024f)
    }

    fun clearAllOfflineTracks() {
        val files = cacheDir.listFiles() ?: return
        for (file in files) {
            file.delete()
        }
    }
}
