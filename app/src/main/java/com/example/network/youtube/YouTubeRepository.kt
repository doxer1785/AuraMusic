package com.example.network.youtube

import android.util.Log
import com.example.BuildConfig
import com.example.data.MusicCatalog
import com.example.model.Track
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class YouTubeRepository {

    private val tag = "YouTubeRepository"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val apiService: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApiService::class.java)
    }

    suspend fun searchMusic(query: String): List<Track> = withContext(Dispatchers.IO) {
        val apiKey = try {
            val key = BuildConfig.YOUTUBE_API_KEY
            if (key.isBlank() || key == "YOUR_YOUTUBE_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }

        // If a direct YouTube link or ID was searched, return the imported track
        val directVideoId = MusicCatalog.extractYouTubeVideoId(query)
        if (directVideoId != null) {
            val directTrack = Track(
                id = "yt_$directVideoId",
                title = "YouTube Video ($directVideoId)",
                artist = "YouTube Music",
                album = "YouTube Single",
                durationMs = 210000,
                genre = "YouTube Music",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                coverGradient = listOf(0xFFFF0000, 0xFF790000, 0xFF121212),
                coverEmoji = "▶️",
                bitrate = "YouTube 1080p HD",
                youtubeVideoId = directVideoId,
                isYouTube = true
            )
            return@withContext listOf(directTrack)
        }

        if (apiKey.isNullOrBlank()) {
            Log.d(tag, "YOUTUBE_API_KEY is not set. Using curated YouTube catalog.")
            return@withContext MusicCatalog.youtubeTracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
            }
        }

        try {
            val response = apiService.searchMusicVideos(
                query = "$query music",
                apiKey = apiKey
            )

            val items = response.items ?: emptyList()
            if (items.isEmpty()) {
                return@withContext emptyList()
            }

            // Fetch durations for found video IDs
            val videoIds = items.mapNotNull { it.id?.videoId }.joinToString(",")
            val detailsMap = try {
                if (videoIds.isNotBlank()) {
                    val detailsResponse = apiService.getVideoDetails(videoIds = videoIds, apiKey = apiKey)
                    detailsResponse.items?.associate { (it.id ?: "") to it } ?: emptyMap()
                } else emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }

            val tracks = items.mapNotNull { item ->
                val videoId = item.id?.videoId ?: return@mapNotNull null
                val snippet = item.snippet ?: return@mapNotNull null
                val rawTitle = cleanHtmlEntities(snippet.title ?: "YouTube Track")
                val channel = cleanHtmlEntities(snippet.channelTitle ?: "YouTube Artist")
                val details = detailsMap[videoId]
                val durationMs = parseIsoDuration(details?.contentDetails?.duration)

                Track(
                    id = "yt_$videoId",
                    title = rawTitle,
                    artist = channel,
                    album = "YouTube Music",
                    durationMs = durationMs,
                    genre = "YouTube Music",
                    audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    coverGradient = listOf(0xFFFF0000, 0xFF790000, 0xFF1A1A1A),
                    coverEmoji = "▶️",
                    bitrate = "YouTube 1080p HD",
                    lyrics = listOf(
                        "Streaming from YouTube Music",
                        rawTitle,
                        "By $channel"
                    ),
                    youtubeVideoId = videoId,
                    isYouTube = true
                )
            }

            tracks
        } catch (e: Exception) {
            Log.e(tag, "Error searching YouTube Data API v3", e)
            // Graceful fallback to local YouTube catalog
            MusicCatalog.youtubeTracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
            }
        }
    }

    private fun cleanHtmlEntities(input: String): String {
        return input
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

    private fun parseIsoDuration(isoDuration: String?): Long {
        if (isoDuration.isNullOrBlank()) return 210000L // Default 3:30
        try {
            val pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
            val matcher = pattern.matcher(isoDuration)
            if (matcher.find()) {
                val hours = matcher.group(1)?.toLongOrNull() ?: 0L
                val minutes = matcher.group(2)?.toLongOrNull() ?: 0L
                val seconds = matcher.group(3)?.toLongOrNull() ?: 0L
                return ((hours * 3600) + (minutes * 60) + seconds) * 1000L
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to parse ISO duration: $isoDuration", e)
        }
        return 210000L
    }
}
