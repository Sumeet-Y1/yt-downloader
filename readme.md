# YT Downloader Backend

A lightweight Spring Boot REST API that downloads YouTube videos as **MP4** or **MP3**, powered by `yt-dlp`. Built as the backend for a full-stack YouTube downloader app, paired with a React + TypeScript frontend.

**Live API:** deployed on [Render](https://render.com)
**Frontend:** deployed on [Cloudflare Pages](https://pages.cloudflare.com)
**Frontend repo:** _link here once public_

---

## ✨ Features

- 🎬 Download YouTube videos as MP4 (video) or MP3 (audio-only)
- ⚡ Single, simple REST endpoint no auth, no session state
- 📦 Streams the converted file straight back as a binary response
- 🐳 Dockerized for easy deployment (Render, Fly.io, Railway, etc.)
- 🌐 CORS-configured to work with a separately hosted frontend

---

## 🧱 Tech Stack

- **Java** + **Spring Boot**
- **yt-dlp** (external binary, invoked as a subprocess)
- **Maven** (build tool, via `mvnw` wrapper)
- **Docker** (containerized deployment)

---

## 📁 Project Structure

```
src/main/java/com/ytdl/app/
├── YtDownloaderApplication.java   # Spring Boot entry point
├── config/
│   └── CorsConfig.java            # CORS setup for frontend access
├── controller/
│   └── DownloadController.java    # Exposes POST /api/download
├── dto/
│   └── DownloadRequest.java       # Request body shape { url, format }
└── service/
    └── DownloadService.java       # Builds & runs the yt-dlp command
```

---

## 🔌 API Reference

### `POST /api/download`

Downloads a YouTube video/audio and returns it as a binary file stream.

**Request body:**
```json
{
  "url": "https://www.youtube.com/watch?v=XXXXXXXXXXX",
  "format": "mp4"
}
```

| Field    | Type                | Description                          |
|----------|---------------------|---------------------------------------|
| `url`    | `string`             | A valid YouTube video URL             |
| `format` | `"mp4"` \| `"mp3"`   | Desired output format                 |

**Success response:** `200 OK`
Binary stream (`video/mp4` or `audio/mpeg`) with a `Content-Disposition` header containing the filename.

**Error response:** non-`200` status with a plain-text error message in the body (e.g. invalid URL, extraction failure, yt-dlp error).

---

## 🚀 Getting Started (Local Development)

### Prerequisites

- **Java 17+**
- **yt-dlp** installed and available on your system `PATH`
  ```bash
  pip install -U yt-dlp
  ```
- **Deno** (recommended  yt-dlp uses it as a JS runtime for reliable YouTube extraction)
  ```bash
  # Windows
  winget install DenoLand.Deno

  # macOS
  brew install deno
  ```
- **ffmpeg** (required by yt-dlp for MP3 conversion / audio extraction)

### Run locally

```bash
git clone https://github.com/Sumeet-Y1/yt-downloader.git
cd yt-downloader

# Run with the Maven wrapper
./mvnw spring-boot:run     # macOS/Linux
mvnw.cmd spring-boot:run   # Windows
```

The API will start on **`http://localhost:8080`**.

### Run with Docker

```bash
docker build -t yt-downloader .
docker run -p 8080:8080 yt-downloader
```

---

## ⚠️ Known Limitation: YouTube Bot Detection

YouTube actively rate-limits and bot-checks requests coming from datacenter IPs (which includes most cloud hosts, Render included). You may occasionally see errors like:

```
ERROR: Sign in to confirm you're not a bot. Use --cookies-from-browser or --cookies for the authentication.
```

**Mitigations in place / recommended:**
- yt-dlp is kept up to date (`yt-dlp -U`) since YouTube changes extraction behavior frequently
- Deno is used as the JS runtime for more reliable extraction
- Exported browser cookies (`--cookies cookies.txt`) can be supplied to `yt-dlp` to reduce bot-check failures

**Note:** Because this is a small, personal-use project rather than a large-scale service, occasional failures during heavy YouTube anti-bot enforcement periods are expected and may require re-exporting cookies or waiting it out.

---

## 🌍 Environment / Deployment Notes

- CORS is configured in `CorsConfig.java` to allow requests from the deployed frontend origin (Cloudflare Pages URL) update this if the frontend origin changes.
- No environment variables are required for basic operation; all configuration is currently in `application.properties`.
- Deployed via Docker on Render; the `Dockerfile` installs `yt-dlp`, `ffmpeg`, and `deno` inside the container image alongside the Spring Boot app.

---

## 📄 License

Not yet decided all rights reserved by default until a license is added.

---

## ⚖️ Disclaimer

This project is intended for personal / educational use. Downloading copyrighted content from YouTube may violate YouTube's Terms of Service and/or copyright law depending on your jurisdiction and how the content is used. Use responsibly.