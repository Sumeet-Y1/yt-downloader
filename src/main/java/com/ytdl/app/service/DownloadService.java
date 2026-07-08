package com.ytdl.app.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DownloadService {

    // Path to the yt-dlp binary. Override with env var YTDLP_PATH if it's not on PATH.
    private final String ytDlpPath = System.getenv().getOrDefault("YTDLP_PATH", "yt-dlp");

    // Where temporary downloads are written before being streamed to the client.
    private final Path workDir = Path.of(System.getProperty("java.io.tmpdir"), "yt-downloader");

    public DownloadService() throws IOException {
        Files.createDirectories(workDir);
    }

    public File download(String url, String format) throws IOException, InterruptedException {
        String jobId = UUID.randomUUID().toString();
        Path jobDir = workDir.resolve(jobId);
        Files.createDirectories(jobDir);

        List<String> command = new ArrayList<>();
        command.add(ytDlpPath);
        command.add("--no-playlist");
        command.add("-o");
        command.add(jobDir.resolve("%(title)s.%(ext)s").toString());

        if ("mp3".equalsIgnoreCase(format)) {
            command.add("-x");
            command.add("--audio-format");
            command.add("mp3");
            command.add("--audio-quality");
            command.add("0");
        } else {
            command.add("-f");
            command.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
            command.add("--merge-output-format");
            command.add("mp4");
        }

        command.add(url);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder log = new StringBuilder();
        try (var reader = process.inputReader()) {
            reader.lines().forEach(line -> log.append(line).append('\n'));
        }

        boolean finished = process.waitFor(15, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            deleteDirectory(jobDir);
            throw new IOException("Download timed out after 15 minutes");
        }
        if (process.exitValue() != 0) {
            deleteDirectory(jobDir);
            throw new IOException("yt-dlp failed:\n" + log);
        }

        Optional<Path> resultFile = Files.list(jobDir).findFirst();
        if (resultFile.isEmpty()) {
            deleteDirectory(jobDir);
            throw new IOException("yt-dlp produced no output file");
        }

        return resultFile.get().toFile();
    }

    public void cleanupJobDir(File file) {
        try {
            Path parent = file.toPath().getParent();
            if (parent != null && parent.startsWith(workDir)) {
                deleteDirectory(parent);
            }
        } catch (Exception ignored) {
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}