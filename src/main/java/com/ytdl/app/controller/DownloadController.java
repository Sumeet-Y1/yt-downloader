package com.ytdl.app.controller;

import com.ytdl.app.dto.DownloadRequest;
import com.ytdl.app.service.DownloadService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class DownloadController {

    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping("/download")
    public ResponseEntity<?> download(@Valid @RequestBody DownloadRequest request) {
        File file;
        try {
            file = downloadService.download(request.getUrl(), request.getFormat());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Download failed: " + e.getMessage());
        }

        String encodedName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        MediaType mediaType = request.getFormat().equalsIgnoreCase("mp3")
                ? MediaType.parseMediaType("audio/mpeg")
                : MediaType.parseMediaType("video/mp4");

        FileSystemResource resource = new FileSystemResource(file);

        Thread cleanupThread = new Thread(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException ignored) {
            }
            downloadService.cleanupJobDir(file);
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentLength(file.length())
                .body(resource);
    }
}