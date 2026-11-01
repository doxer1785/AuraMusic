# AuraMusic: High-Fidelity Music Streaming & Party Sync

AuraMusic is a next-generation music streaming experience inspired by Spotify and Apple Music, featuring real streaming playback, an offline caching engine, personalized daily discovery playlists, real-time social listening, cross-device sync, and a synchronous Party Mode supporting 10+ connected guests via QR code and room PIN.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were confirmed during the interactive interview:
> - **Catalog & Audio Source**: High-res streaming catalog curated across indie, lofi, pop, and electronic genres with high-fidelity streams and audio quality toggles (Lossless Hi-Fi / Balanced / Data Saver).
> - **Party Mode & Multi-Device Sync**: Hybrid local and cloud room architecture using a 6-digit Room PIN and dynamic QR code, allowing hosts to stream and broadcast real-time playback state, queue, and live reactions to 10+ connected listener phones.
> - **Visual Aesthetic**: Vibrant dynamic album-art gradient theme with glassmorphism card surfaces, glowing playback controls, and animated waveform visualizers.

---

## 1. Overview & Core Concept

- **What It Does**: AuraMusic delivers an integrated audio streaming platform where users can discover tailored daily playlists, stream high-fidelity tracks with interactive waveforms, download music for seamless offline playback, share favorites with friends, and host or join synchronized multi-device party sessions.
- **Target Audience / Persona**: Music enthusiasts, casual listeners, and social party hosts who want seamless audio transitions between solo listening and group party jams.
- **Key Value**: Combines the polished fluidity of Apple Music and Spotify with an interactive synchronized party listening experience that connects friends without requiring complex hardware setups.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Explore & Daily Discovery**:
   - Dynamic greeting with personalized recommendation shelves: *Daily Mix 1 (Lofi Chill)*, *Daily Mix 2 (Indie Dream)*, *Daily Mix 3 (Synth & Electronic)*, *Discover Weekly*, *Heavy Rotation*, and *Trending Now*.
   - Filter chips for quick genre/mood switching (All, Chill, Energy, Focus, Party, Workout).
2. **Now Playing & Dynamic Gradient Player**:
   - Mini-player docked above the navigation bar with swipe gestures, smooth expand to full-screen player.
   - Dynamic background gradient that shifts based on the current album art's dominant colors (e.g. Neon Emerald, Velvet Violet, Cyberpunk Amber, Electric Blue).
   - Audio waveform visualizer reacting to playback progress, scrubbing slider, repeat/shuffle, volume, audio quality selector, and lyrics view.
3. **Offline Mode & Downloads**:
   - Single-tap download on any song, album, or playlist with instant progress indicator.
   - Dedicated "Offline Only" toggle switch in the Library to filter and play stored local music without network connectivity.
4. **Party Mode (Listen Together for 10+ Phones)**:
   - **Host Flow**: Tap "Start Party Mode", which creates a lobby with a unique 6-digit Room PIN and shareable QR code. Host controls live playback (play/pause/skip), manages the collaborative queue, and sees live participant count and floating emoji reactions.
   - **Guest Flow**: Tap "Join Party", enter the 6-digit PIN or scan QR code. Guest devices lock into the host's playback stream in real time, show synced track details, and can cast live emoji reactions (🔥, 💃, 🎧, ❤️, ⚡) and submit song requests into the room queue.
5. **Social & Friends Activity**:
   - Dedicated Friends tab showing active friends, current track they are listening to, and timestamp.
   - 1-tap "Listen Along" to jump into a friend's stream, or "Share Song" to open a customizable story card / direct link modal.
6. **Cross-Device Sync & Audio Quality**:
   - "Connect to a Device" sheet (Phone, Living Room Echo, Desktop App, Studio Monitors, Car Bluetooth) with instant seamless transfer.

### Visual Identity & Theme
- **Theme**: Dark Luxury & Dynamic Radiant Glow (Deep OLED Black `#0B0C10` background with radiant neon highlights `#1DB954`, `#6C5CE7`, `#FD79A8`, and `#00CEC9`).
- **Surfaces**: Semi-transparent frosted glass cards (`Color(0x22FFFFFF)`) with subtle hairline borders (`Color(0x33FFFFFF)`).
- **Typography**: Clean, geometric modern typography with bold track titles, legible metadata labels, and high contrast.
- **Motion**: Spring animations for play/pause toggle, slide-up transitions for the full-screen player modal, and floating reaction particle effects in Party Mode.

---

## 3. Key Product Decisions & Trade-Offs

- **Audio Engine (Native Android MediaPlayer & Background Service)**:
  - *Chosen Approach*: Android SDK native `MediaPlayer` integrated within an application-scoped audio manager and foreground-ready media architecture.
  - *Why*: Reliable, immediate audio playback with support for remote HTTPS audio streams and local cached file URIs, zero native library incompatibility, and instant response times.
- **Offline Download Manager**:
  - *Chosen Approach*: OkHttp-backed binary audio caching into app-specific private storage (`context.filesDir/music_cache`), recording metadata in a local repository with checksum verification.
  - *Why*: Fast background caching, reliable offline playback without requiring cellular/Wi-Fi data, and no invasive system storage permissions required.
- **Party Mode Architecture (Hybrid Local/Cloud Sync Engine)**:
  - *Chosen Approach*: Reactive room state coordinator that manages room creation, PIN/QR generation, connected device roster (supporting 10+ devices), synchronized timeline clock drift compensation, collaborative queue, and live reaction broadcasts.
  - *Why*: Guarantees instant local responsiveness with zero authentication hurdles while providing real-time room synchronization for multi-device party jams.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              AuraMusic UI                              │
│ ┌───────────────┐ ┌────────────────┐ ┌───────────────┐ ┌─────────────┐ │
│ │  Home Screen  │ │  Party Lobby   │ │  Search/Genre │ │   Library   │ │
│ │ (Daily Mixes) │ │ (PIN & QR Host)│ │   & Social    │ │ & Downloads │ │
│ └───────┬───────┘ └───────┬────────┘ └───────┬───────┘ └──────┬──────┘ │
│         │                 │                  │                │        │
│ ┌───────┴─────────────────┴──────────────────┴────────────────┴──────┐ │
│ │                  Mini Player & Full Now-Playing Modal              │ │
│ └────────────────────────────────┬───────────────────────────────────┘ │
└──────────────────────────────────┼─────────────────────────────────────┘
                                   │
┌──────────────────────────────────▼─────────────────────────────────────┐
│                          MusicViewModel                                │
│   - PlaybackState (Track, Progress, IsPlaying, Quality, Repeat/Shuffle) │
│   - PartyRoomState (RoomCode, HostId, Participants, Queue, Reactions)  │
│   - OfflineState (DownloadedTrackIds, StorageUsage, OfflineFilter)     │
│   - SocialState (FriendsActivity, SharedTracks)                        │
└──────────────┬───────────────────┬───────────────────┬─────────────────┘
               │                   │                   │
┌──────────────▼──────┐   ┌────────▼──────────┐   ┌────▼─────────────────┐
│     AudioEngine     │   │ PartySyncManager  │   │ OfflineDownloadMgr   │
│ - Android MediaPlayer│  │ - Room Lifecycle  │   │ - OkHttp file cache  │
│ - Stream/Local URI  │   │ - Clock Sync      │   │ - Local storage      │
│ - Seek / Buffering  │   │ - Guest Roster    │   │ - Offline validator  │
│ - Waveform emitter  │   │ - Reaction stream │   │                      │
└─────────────────────┘   └───────────────────┘   └──────────────────────┘
```

### Data Models
- **`Track`**: `id`, `title`, `artist`, `album`, `coverUrl`, `audioUrl`, `durationMs`, `genre`, `dominantColor`, `secondaryColor`, `isDownloaded`, `bitrateKbps`.
- **`Playlist`**: `id`, `title`, `description`, `coverUrl`, `tracks`, `curator`, `isDailyDiscovery`.
- **`PartyRoom`**: `roomCode`, `hostName`, `currentTrack`, `playbackPositionMs`, `isPlaying`, `guestCount`, `queue`, `recentReactions`.
- **`FriendActivity`**: `id`, `name`, `avatarUrl`, `listeningTrack`, `timestamp`, `isOnline`.

---

## Verification Plan

### Compilation and Build Verification
- Run `compile_applet` to verify zero Kotlin syntax errors, correct resource references, and flawless Compose compatibility.

### Functional Verification
- Verify audio playback controls (play, pause, seek, next, previous, shuffle, repeat).
- Verify dynamic gradient backdrop calculation matching active track.
- Verify download flow (downloading a track, switching to Offline Mode, and playing without stream).
- Verify Party Mode: create a party room, generate PIN and QR code, connect simulated guest devices (up to 10+), push guest reactions, and manage party queue.
- Verify Social tab: browse friends listening status, trigger "Listen Along", and open track share preview.
