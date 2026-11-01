package com.example.data

import com.example.model.DeviceType
import com.example.model.FriendActivity
import com.example.model.Playlist
import com.example.model.SyncDevice
import com.example.model.Track

object MusicCatalog {

    val sampleTracks = listOf(
        Track(
            id = "track_1",
            title = "Midnight Horizon",
            artist = "Aura Chill Project",
            album = "Neon Drift EP",
            durationMs = 214000,
            genre = "Synthwave",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            coverGradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
            coverEmoji = "🌆",
            bitrate = "320 kbps (Hi-Fi)",
            bpm = 118,
            lyrics = listOf(
                "Cruising down the empty neon avenue",
                "City lights reflecting in the rearview",
                "Feel the bassline pulsating through the floor",
                "Chasing morning light forever more",
                "Electric dreams and synthetic memories",
                "Lost inside the endless melodies"
            ),
            isLiked = true,
            playCount = 428
        ),
        Track(
            id = "track_2",
            title = "Emerald Sunset",
            artist = "Solaris Echo",
            album = "Subtle Moments",
            durationMs = 186000,
            genre = "Lofi & Chill",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            coverGradient = listOf(0xFF00B09B, 0xFF96C93D),
            coverEmoji = "🍃",
            bitrate = "320 kbps (Hi-Fi)",
            bpm = 84,
            lyrics = listOf(
                "Warm cup of coffee on the windowsill",
                "Rain gently tapping while the room is still",
                "Tape warble humming a forgotten song",
                "Where we used to dance all night long",
                "Slow down the ticking clock",
                "Just breathe and let the feeling lock"
            ),
            isLiked = true,
            playCount = 890
        ),
        Track(
            id = "track_3",
            title = "Starlight Odyssey",
            artist = "Luna & The Astronauts",
            album = "Cosmic Reverie",
            durationMs = 245000,
            genre = "Indie Pop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            coverGradient = listOf(0xFF654EA3, 0xFFEAAFC8),
            coverEmoji = "✨",
            bitrate = "320 kbps (Lossless)",
            bpm = 124,
            lyrics = listOf(
                "Floating through gravitational pull",
                "Stars so bright and the sky so full",
                "Take my hand into the stellar drift",
                "Feel the heavy weight begin to lift",
                "We don't need a map or a destination",
                "Only pure sonic vibration"
            ),
            isLiked = false,
            playCount = 312
        ),
        Track(
            id = "track_4",
            title = "Cyber Velocity",
            artist = "Glitchrunner",
            album = "Overdrive 2099",
            durationMs = 198000,
            genre = "Electronic",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            coverGradient = listOf(0xFF7F00FF, 0xFFE100FF),
            coverEmoji = "⚡",
            bitrate = "320 kbps (Lossless)",
            bpm = 132,
            lyrics = listOf(
                "Data stream racing across the grid",
                "Secrets that the digital shadows hid",
                "Accelerate past the firewall gate",
                "No turning back, surrender to fate",
                "Hyperdrive engaged at maximum speed",
                "Pure adrenaline is all we need"
            ),
            isLiked = false,
            playCount = 574
        ),
        Track(
            id = "track_5",
            title = "Pacific Blue Waves",
            artist = "Marina Bay",
            album = "Coastal Highway",
            durationMs = 222000,
            genre = "Acoustic Chill",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            coverGradient = listOf(0xFF2193B0, 0xFF6DD5ED),
            coverEmoji = "🌊",
            bitrate = "320 kbps (Hi-Fi)",
            bpm = 95,
            lyrics = listOf(
                "Salt in the air, wind through the trees",
                "Drifting along with the ocean breeze",
                "Golden sunshine kissing the sandy shore",
                "Couldn't ask for anything more",
                "Watching the tide roll in and out",
                "This is what life is all about"
            ),
            isLiked = true,
            playCount = 612
        ),
        Track(
            id = "track_6",
            title = "Golden Hour Mirage",
            artist = "Velvet Mirage",
            album = "Sunset Boulevard",
            durationMs = 205000,
            genre = "R&B / Soul",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            coverGradient = listOf(0xFFFA709A, 0xFFFEE140),
            coverEmoji = "🌅",
            bitrate = "320 kbps (Lossless)",
            bpm = 90,
            lyrics = listOf(
                "Shadows stretch across the golden wall",
                "Waiting for your midnight telephone call",
                "Silky guitar notes fill the room",
                "Banishing all traces of the gloom",
                "Just you and me in the twilight glow",
                "Let the sweet melody slowly flow"
            ),
            isLiked = false,
            playCount = 188
        ),
        Track(
            id = "track_7",
            title = "Tokyo Rain Reflections",
            artist = "Komorebi",
            album = "Shibuya Nights",
            durationMs = 175000,
            genre = "Lofi & Chill",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            coverGradient = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
            coverEmoji = "🌧️",
            bitrate = "320 kbps (Hi-Fi)",
            bpm = 80,
            lyrics = listOf(
                "Umbrellas blooming in neon pink",
                "Drip drop rhythms make you stop and think",
                "Vending machine glowing by the alleyway",
                "Waiting for the dawn of another day",
                "Quiet footsteps on the wet concrete",
                "Synchronized to the lofi beat"
            ),
            isLiked = true,
            playCount = 940
        ),
        Track(
            id = "track_8",
            title = "Club Voltage",
            artist = "DJ Pulse & K-Wave",
            album = "Party Transmission",
            durationMs = 230000,
            genre = "Party Dance",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
            coverGradient = listOf(0xFFFF0844, 0xFFFFB199),
            coverEmoji = "🔥",
            bitrate = "320 kbps (Lossless)",
            bpm = 128,
            lyrics = listOf(
                "Turn the volume all the way to ten",
                "Drop the bass and run it back again",
                "Hands in the air under the laser beam",
                "Living inside a wild party dream",
                "Everybody jump when the breakdown hits",
                "This entire club is rocking to bits"
            ),
            isLiked = true,
            playCount = 1420
        )
    )

    val youtubeTracks = listOf(
        Track(
            id = "yt_1",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationMs = 200000,
            genre = "Synthpop / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            coverGradient = listOf(0xFFFF0000, 0xFF790000, 0xFF1A1A1A),
            coverEmoji = "🕶️",
            bitrate = "YouTube 1080p HD",
            bpm = 171,
            lyrics = listOf(
                "I've been on my own for long enough",
                "Maybe you can show me how to love, maybe",
                "I'm going through withdrawals",
                "You don't even have to do too much",
                "You can turn me on with just a touch, baby",
                "I look around and Sin City's cold and empty",
                "No one's around to judge me",
                "I can't see clearly when you're gone",
                "I said, ooh, I'm blinded by the lights"
            ),
            isLiked = true,
            youtubeVideoId = "4NRXx6U8ABQ",
            isYouTube = true,
            playCount = 3890
        ),
        Track(
            id = "yt_2",
            title = "Espresso",
            artist = "Sabrina Carpenter",
            album = "Short n' Sweet",
            durationMs = 175000,
            genre = "Pop / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            coverGradient = listOf(0xFF00C0FF, 0xFFFF70A6, 0xFFFFE066),
            coverEmoji = "☕",
            bitrate = "YouTube 1080p HD",
            bpm = 104,
            lyrics = listOf(
                "Now he's thinkin' 'bout me every night, oh",
                "Is it that sweet? I guess so",
                "Say you can't sleep, baby, I know",
                "That's that me espresso",
                "Move it up, down, left, right, oh",
                "Switch it up like Nintendo"
            ),
            isLiked = true,
            youtubeVideoId = "eVli-tstM5E",
            isYouTube = true,
            playCount = 2740
        ),
        Track(
            id = "yt_3",
            title = "Levitating",
            artist = "Dua Lipa",
            album = "Future Nostalgia",
            durationMs = 203000,
            genre = "Disco Pop / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            coverGradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
            coverEmoji = "✨",
            bitrate = "YouTube 1080p HD",
            bpm = 103,
            lyrics = listOf(
                "If you wanna run away with me, I know a galaxy",
                "And I can take you for a ride",
                "I had a premonition that we fell into a rhythm",
                "Where the music don't stop for life",
                "Glitter in the sky, glitter in my eyes",
                "Shining just the way I like",
                "If you're feeling like you need a little bit of company",
                "You met me at the perfect time",
                "You want me, I want you, baby",
                "My sugarboo, I'm levitating"
            ),
            isLiked = false,
            youtubeVideoId = "TUVcZfQe-Kw",
            isYouTube = true,
            playCount = 1920
        ),
        Track(
            id = "yt_4",
            title = "Birds of a Feather",
            artist = "Billie Eilish",
            album = "HIT ME HARD AND SOFT",
            durationMs = 196000,
            genre = "Alternative / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            coverGradient = listOf(0xFF1B3B6F, 0xFF6D9DC5, 0xFF21295C),
            coverEmoji = "🕊️",
            bitrate = "YouTube 1080p HD",
            bpm = 105,
            lyrics = listOf(
                "I want you to stay",
                "'Til I'm in the grave",
                "'Til I rot away, dead and buried",
                "'Til I'm in the casket you carry",
                "If you go, I'm going too, uh",
                "'Cause it was always you, alright"
            ),
            isLiked = true,
            youtubeVideoId = "d5gx4h_b8G4",
            isYouTube = true,
            playCount = 3120
        ),
        Track(
            id = "yt_5",
            title = "Die With A Smile",
            artist = "Lady Gaga & Bruno Mars",
            album = "Single",
            durationMs = 251000,
            genre = "Ballad / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            coverGradient = listOf(0xFF4A0E4E, 0xFFE056FD, 0xFFF8A5C2),
            coverEmoji = "🎸",
            bitrate = "YouTube 1080p HD",
            bpm = 104,
            lyrics = listOf(
                "If the world was ending, I'd wanna be next to you",
                "If the party was over and our time on Earth was through",
                "I'd wanna hold you just for a while",
                "And die with a smile",
                "If the world was ending, I'd wanna be next to you"
            ),
            isLiked = true,
            youtubeVideoId = "kPa7bsKwL-8",
            isYouTube = true,
            playCount = 4210
        ),
        Track(
            id = "yt_6",
            title = "Flowers",
            artist = "Miley Cyrus",
            album = "Endless Summer Vacation",
            durationMs = 200000,
            genre = "Pop / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            coverGradient = listOf(0xFFF6D365, 0xFFFDA085),
            coverEmoji = "🌸",
            bitrate = "YouTube 1080p HD",
            bpm = 118,
            lyrics = listOf(
                "We were good, we were gold",
                "Kinda dream that can't be sold",
                "We were right 'til we weren't",
                "Built a home and watched it burn",
                "I can buy myself flowers",
                "Write my name in the sand",
                "Talk to myself for hours",
                "Say things you don't understand"
            ),
            isLiked = false,
            youtubeVideoId = "G7KNmW9a75Y",
            isYouTube = true,
            playCount = 2890
        ),
        Track(
            id = "yt_7",
            title = "Shape of You",
            artist = "Ed Sheeran",
            album = "Divide",
            durationMs = 233000,
            genre = "Pop / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            coverGradient = listOf(0xFF00B4DB, 0xFF0083B0),
            coverEmoji = "➗",
            bitrate = "YouTube 1080p HD",
            bpm = 96,
            lyrics = listOf(
                "The club isn't the best place to find a lover",
                "So the bar is where I go",
                "Me and my friends at the table doing shots",
                "Drinking fast and then we talk slow",
                "I'm in love with the shape of you",
                "We push and pull like a magnet do"
            ),
            isLiked = false,
            youtubeVideoId = "JGwWNGJdvx8",
            isYouTube = true,
            playCount = 5120
        ),
        Track(
            id = "yt_8",
            title = "Starboy",
            artist = "The Weeknd ft. Daft Punk",
            album = "Starboy",
            durationMs = 230000,
            genre = "R&B / YouTube",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
            coverGradient = listOf(0xFF141E30, 0xFF243B55, 0xFFFF0000),
            coverEmoji = "⚡",
            bitrate = "YouTube 1080p HD",
            bpm = 186,
            lyrics = listOf(
                "I'm tryna put you in the worst mood, ah",
                "P1 cleaner than your church shoes, ah",
                "Milli point two just to hurt you, ah",
                "All red Lamb' just to tease you, ah",
                "Look what you've done",
                "I'm a motherfucking starboy"
            ),
            isLiked = true,
            youtubeVideoId = "34Na4j8AVgA",
            isYouTube = true,
            playCount = 4900
        )
    )

    val allCatalogTracks = sampleTracks + youtubeTracks

    val samplePlaylists = listOf(
        Playlist(
            id = "daily_mix_1",
            title = "Daily Mix 1",
            description = "Lofi study beats, warm vinyl crackle, and gentle acoustic rhythms.",
            curator = "Made for You",
            gradient = listOf(0xFF00B09B, 0xFF96C93D),
            coverEmoji = "☕",
            tracks = listOf(sampleTracks[1], sampleTracks[6], sampleTracks[4]),
            isDailyDiscovery = true,
            category = "Daily Discovery"
        ),
        Playlist(
            id = "daily_mix_2",
            title = "Daily Mix 2",
            description = "Dreamy indie pop, reverberating guitars, and floating vocal harmonies.",
            curator = "Made for You",
            gradient = listOf(0xFF654EA3, 0xFFEAAFC8),
            coverEmoji = "🎸",
            tracks = listOf(sampleTracks[2], sampleTracks[5], sampleTracks[0]),
            isDailyDiscovery = true,
            category = "Daily Discovery"
        ),
        Playlist(
            id = "daily_mix_3",
            title = "Daily Mix 3",
            description = "Retro synthwave, dark techno, and high-octane cyberpunk club beats.",
            curator = "Made for You",
            gradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
            coverEmoji = "⚡",
            tracks = listOf(sampleTracks[0], sampleTracks[3], sampleTracks[7]),
            isDailyDiscovery = true,
            category = "Daily Discovery"
        ),
        Playlist(
            id = "discover_weekly",
            title = "Discover Weekly",
            description = "Your weekly mixtape of fresh finds and undiscovered gems updated every Monday.",
            curator = "Aura Algorithm",
            gradient = listOf(0xFF1DB954, 0xFF191414),
            coverEmoji = "🔮",
            tracks = listOf(sampleTracks[3], sampleTracks[1], sampleTracks[5], sampleTracks[2]),
            isDailyDiscovery = true,
            category = "Curated For You"
        ),
        Playlist(
            id = "party_mode_favorites",
            title = "Party Mode Bangers",
            description = "Peak time anthems curated for multi-phone synchronized jam sessions.",
            curator = "Aura Party DJ",
            gradient = listOf(0xFFFF0844, 0xFFFFB199),
            coverEmoji = "🎉",
            tracks = listOf(sampleTracks[7], sampleTracks[3], sampleTracks[0], sampleTracks[2]),
            isDailyDiscovery = false,
            category = "Party & Group"
        ),
        Playlist(
            id = "chill_vibes",
            title = "Deep Focus & Chill",
            description = "Subtle instrumental waves to keep your mind locked in the flow state.",
            curator = "Editorial Team",
            gradient = listOf(0xFF2193B0, 0xFF6DD5ED),
            coverEmoji = "🧘",
            tracks = listOf(sampleTracks[1], sampleTracks[4], sampleTracks[6]),
            isDailyDiscovery = false,
            category = "Mood & Focus"
        ),
        Playlist(
            id = "youtube_top_hits",
            title = "YouTube Music Global Top",
            description = "The biggest global music videos and streaming anthems on YouTube.",
            curator = "YouTube Music Charts",
            gradient = listOf(0xFFFF0000, 0xFF282828),
            coverEmoji = "▶️",
            tracks = youtubeTracks,
            isDailyDiscovery = false,
            category = "YouTube Music"
        )
    )

    fun extractYouTubeVideoId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.length == 11 && trimmed.matches(Regex("[a-zA-Z0-9_-]{11}"))) {
            return trimmed
        }
        val patterns = listOf(
            Regex("(?:youtube\\.com\\/watch\\?v=|youtu\\.be\\/|youtube\\.com\\/embed\\/|youtube\\.com\\/shorts\\/|music\\.youtube\\.com\\/watch\\?v=)([a-zA-Z0-9_-]{11})"),
            Regex("v=([a-zA-Z0-9_-]{11})")
        )
        for (pattern in patterns) {
            val match = pattern.find(trimmed)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1]
            }
        }
        return null
    }

    val sampleFriends = listOf(
        FriendActivity(
            id = "f_1",
            name = "Alex Vance",
            username = "@alex_v",
            avatarColor = 0xFF7C4DFF,
            listeningTrack = sampleTracks[0],
            timeAgo = "Listening now",
            isLive = true,
            playlistName = "Daily Mix 3"
        ),
        FriendActivity(
            id = "f_2",
            name = "Maya Lin",
            username = "@maya_sounds",
            avatarColor = 0xFF00E5FF,
            listeningTrack = sampleTracks[1],
            timeAgo = "3m ago",
            isLive = true,
            playlistName = "Study Session"
        ),
        FriendActivity(
            id = "f_3",
            name = "Liam Chen",
            username = "@liam_c",
            avatarColor = 0xFFFF4081,
            listeningTrack = sampleTracks[7],
            timeAgo = "12m ago",
            isLive = false,
            playlistName = "Party Mode Bangers"
        ),
        FriendActivity(
            id = "f_4",
            name = "Sophia Rossi",
            username = "@sophia_beats",
            avatarColor = 0xFFFFAB00,
            listeningTrack = sampleTracks[2],
            timeAgo = "25m ago",
            isLive = false,
            playlistName = "Discover Weekly"
        )
    )

    val sampleDevices = listOf(
        SyncDevice(
            id = "dev_1",
            name = "Pixel 8 Pro (This Device)",
            type = DeviceType.PHONE,
            isCurrent = true,
            batteryPercent = 88,
            volumePercent = 70
        ),
        SyncDevice(
            id = "dev_2",
            name = "Living Room Soundbar (AuraCast)",
            type = DeviceType.SPEAKER,
            isCurrent = false,
            batteryPercent = null,
            volumePercent = 65
        ),
        SyncDevice(
            id = "dev_3",
            name = "MacBook Pro M3",
            type = DeviceType.COMPUTER,
            isCurrent = false,
            batteryPercent = 94,
            volumePercent = 50
        ),
        SyncDevice(
            id = "dev_4",
            name = "Tesla Model 3 Audio",
            type = DeviceType.CAR,
            isCurrent = false,
            batteryPercent = null,
            volumePercent = 80
        )
    )
}
