package com.mjfelecio.beatsync.utils;

import java.io.IOException;

public class AudioConverter {
    public static boolean convertOggToMp3(String inputPath, String outputPath) {
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
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        boolean success = AudioConverter.convertOggToMp3("src/main/resources/com/mjfelecio/beatsync/beatmaps/1996162 Akiri X HowToPlayLN - Kuroi Hanabira.osz_FILES/audio.ogg", "src/main/resources/com/mjfelecio/beatsync/beatmaps/1996162 Akiri X HowToPlayLN - Kuroi Hanabira.osz_FILES/audio.mp3");
        System.out.println("Conversion successful? " + success);
    }

}
