package com.mjfelecio.beatsync.utils;

import java.io.IOException;

public class AudioConverter {
    public static void convertOggToMp3(String inputPath, String outputPath) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg", "-y",  // Overwrite output file if it exists
                "-i", inputPath,            // Input file
                "-codec:a", "libmp3lame",   // Use LAME encoder for MP3
                "-qscale:a", "2",           // Quality setting (lower = better; 2 ≈ 190kbps)
                outputPath
        );

        builder.inheritIO();

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg exited with non-zero code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IOException(String.format("Failed to convert %s to .mp3", inputPath), e);
        }
    }
}
