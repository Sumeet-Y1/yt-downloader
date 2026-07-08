package com.ytdl.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DownloadRequest {

    @NotBlank(message = "url is required")
    // Basic guard so this endpoint can only ever be pointed at YouTube, not arbitrary sites.
    @Pattern(
            regexp = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]+.*$",
            message = "Only YouTube URLs are allowed"
    )
    private String url;

    @NotBlank(message = "format is required")
    @Pattern(regexp = "^(mp4|mp3)$", message = "format must be 'mp4' or 'mp3'")
    private String format;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}