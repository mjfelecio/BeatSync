package com.mjfelecio.beatsync.utils;

//import com.github.kokorin.jaffree.StreamType;
//import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
//import com.github.kokorin.jaffree.ffmpeg.UrlInput;
//import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.mjfelecio.beatsync.config.GameConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    /**
     * Extracts the contents of a ZIP file to a specified destination directory.
     * A new directory named after the ZIP file (without the .zip extension) will be created.
     *
     * @param zipFilePath      the path to the ZIP file to be extracted
     * @param destDirectoryPath the path to the destination directory where files will be extracted
     * @throws IOException if an I/O error occurs during extraction
     */
    public static boolean extractZipFile(String zipFilePath, String destDirectoryPath) {
        File zipFile = new File(zipFilePath);
        File tempDestDir = null;

        try {
            // Prepare temp directory
            tempDestDir = File.createTempFile("extract_", "");
            if (!tempDestDir.delete() || !tempDestDir.mkdir()) {
                throw new IOException("Failed to create temporary extraction directory");
            }

            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;

                while ((entry = zipInputStream.getNextEntry()) != null) {
                    File newFile = newFile(tempDestDir, entry);
                    if (newFile == null) {
                        throw new IOException("Invalid file or unsupported type: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        if (!newFile.mkdirs()) {
                            throw new IOException("Failed to create directory: " + newFile.getAbsolutePath());
                        }
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }
                    }

                    zipInputStream.closeEntry();
                }
            }

            // Move temp directory to final location
            File finalDestDir = getDestDir(destDirectoryPath, zipFile);
            if (!tempDestDir.renameTo(finalDestDir)) {
                throw new IOException("Failed to move extracted files to final destination");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Extraction failed: " + e.getMessage());
            return false;
        } finally {
            // Clean up on failure
            if (tempDestDir != null && tempDestDir.exists()) {
                deleteRecursively(tempDestDir);
            }
        }
    }

    private static File getDestDir(String destDirectoryPath, File zipFile) throws IOException {
        String zipFileName = zipFile.getName();
        String baseName = zipFileName.substring(0, zipFileName.lastIndexOf('.'));

        // Create the destination directory for the extracted files
        File destDir = new File(destDirectoryPath, baseName);

        // Create the destination directory if it does not exist
        if (!destDir.exists()) {
            if (!destDir.mkdirs()) {
                throw new IOException("Failed to create destination directory: " + destDir.getAbsolutePath());
            }
        }

        // Ensure the destination directory is a directory
        if (!destDir.isDirectory()) {
            throw new IOException("Destination path is not a directory: " + destDir.getAbsolutePath());
        }
        return destDir;
    }

    /**
     * Creates a new file in the destination directory based on the ZipEntry.
     * This method prevents Zip Slip vulnerabilities by ensuring the file is within the target directory.
     *
     * @param destinationDir the destination directory
     * @param zipEntry      the ZipEntry to create a file for
     * @return the new File object
     * @throws IOException if the file path is outside the target directory
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        if (!isValidFile(destFile)) {
            return null;
        }

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static boolean isValidFile(File file) {
        if (file.getName().endsWith(".ogg")) {
//            destFile = convertOGGToMP3(destFile);
            System.err.println(".ogg audio file is currently not supported");
            return false;
        }

        return true;
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

//    private static File convertOGGToMP3(File oggFile) {
//        File mp3File = new File(oggFile.getParent(), oggFile.getName().replaceAll("\\.ogg$", ".mp3"));
//
//        FFmpeg.atPath() // assumes ffmpeg is in system PATH
//                .addInput(UrlInput.fromPath(oggFile.toPath()))
//                .addOutput(UrlOutput.toPath(mp3File.toPath())
//                        .setCodec(StreamType.AUDIO, "libmp3lame"))
//                .execute();
//
//        // Optionally delete the .ogg file after conversion
//        oggFile.delete();
//
//        return mp3File;
//    }

    // WTF I spent so long on this and I just realized I don't even need it
//    /**
//     * Renames the extracted beatmap folder to it's beatmap title
//     * using BeatmapSetParser.parse(file).getTitle()
//     *
//     * @param file the extracted .osz file that we want to give a name
//     * @throws IOException if operation was unsuccessful
//     */
//    private static void renameBeatmapFolder(File file) throws IOException {
//        Path source = Path.of(file.getAbsolutePath());
//        String newName = BeatmapSetParser.parse(file).getTitle();
//        Files.move(source, source.resolveSibling(newName));
//    }

    private static boolean checkIfOsz(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        String fileExtension = "";

        if (lastDotIndex != -1) {
            fileExtension = fileName.substring(lastDotIndex).trim();
        }

        return fileExtension.equals(".osz");
    }

    // I just run this in case I need to import new maps
    public static void main(String[] args) throws IOException {
        String oszToImportFilePath = GameConfig.BEATMAP_DIRECTORY;
        String destinationFilePath = GameConfig.BEATMAP_DIRECTORY;
        ArrayList<String> successfulExtractions = new ArrayList<>();
        ArrayList<String> failedExtractions = new ArrayList<>();

        File[] oszFiles = Arrays.stream(Objects.requireNonNull(new File(oszToImportFilePath).listFiles()))
                .filter(ZipExtractor::checkIfOsz)
                .toList().toArray(new File[0]);

        for (File file : oszFiles) {
            boolean success = ZipExtractor.extractZipFile(file.getAbsolutePath(), destinationFilePath);

            if (success) {
                successfulExtractions.add(file.getName());
                file.delete(); // Delete the osz file once it has been extracted successfully
            } else {
                failedExtractions.add(file.getName());
            }
        }

        // Extraction report
        System.out.println("Successfully imported these files:");
        successfulExtractions.forEach(s -> System.out.println("--> " + s));

        System.out.println("Failed to import these files:");
        failedExtractions.forEach(s -> System.out.println("--> " + s));
    }
}
