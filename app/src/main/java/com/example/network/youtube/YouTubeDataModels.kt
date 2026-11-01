package com.example.network.youtube

import com.squareup.moshi.Json

data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeSearchResultItem>? = emptyList(),
    @Json(name = "nextPageToken") val nextPageToken: String? = null
)

data class YouTubeSearchResultItem(
    @Json(name = "id") val id: YouTubeResourceId? = null,
    @Json(name = "snippet") val snippet: YouTubeSnippet? = null
)

data class YouTubeResourceId(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "videoId") val videoId: String? = null
)

data class YouTubeSnippet(
    @Json(name = "title") val title: String? = "",
    @Json(name = "description") val description: String? = "",
    @Json(name = "channelTitle") val channelTitle: String? = "",
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails? = null,
    @Json(name = "publishedAt") val publishedAt: String? = null
)

data class YouTubeThumbnails(
    @Json(name = "default") val defaultThumb: YouTubeThumbnailInfo? = null,
    @Json(name = "medium") val mediumThumb: YouTubeThumbnailInfo? = null,
    @Json(name = "high") val highThumb: YouTubeThumbnailInfo? = null
)

data class YouTubeThumbnailInfo(
    @Json(name = "url") val url: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

data class YouTubeVideoListResponse(
    @Json(name = "items") val items: List<YouTubeVideoDetailsItem>? = emptyList()
)

data class YouTubeVideoDetailsItem(
    @Json(name = "id") val id: String? = null,
    @Json(name = "snippet") val snippet: YouTubeSnippet? = null,
    @Json(name = "contentDetails") val contentDetails: YouTubeContentDetails? = null
)

data class YouTubeContentDetails(
    @Json(name = "duration") val duration: String? = null
)
