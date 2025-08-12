package com.piLovr.messenger_interop_starter.util;

import org.javatuples.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MediaConverter {
    private static Map<Pair<String, String>, String> execs = new HashMap<>(
            Map.of(
                    //Image to sticker
                    Pair.with("image/jpeg", "image/webp"), "cwebp %s -o %s",
                    //Video or gif to animated sticker
                    Pair.with("video/mp4", "image/webp"), "ffmpeg -i %s -vcodec libwebp -filter:v fps=20,scale=512:512:force_original_aspect_ratio=decrease,format=rgba,pad=512:512:-1:-1:color=black@0.0 -loop 0 -ss 0 -t 5 -an -preset default -y %s",

                    //sticker to image
                    Pair.with("image/webp", "image/jpeg"), "dwebp %s -o %s",
                    //animated sticker to gif
                    Pair.with("image/webp", "image/gif"), "magick %s -coalesce -layers optimize %s"
            )
    );
    private static byte[] mediaConverter(byte[] inputMedia, String inputMime, String expectedMime){
        String inputExtension = inputMime.substring(inputMime.lastIndexOf('/') + 1);
        String expectedExtension = expectedMime.substring(expectedMime.lastIndexOf('/') + 1);

        Path tempInputFile;
        Path tempOutputFile;

        //create temp file
        try {
            tempInputFile = Files.createTempFile("input.", inputExtension);
            tempOutputFile = Files.createTempFile("output.", expectedExtension);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp files", e);
        }
        //write in temp file
        try {
            Files.write(tempInputFile, inputMedia);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to temp input file", e);
        }
        //create output temp file

        //close output temp file

        //run exec stuff
        String exec = execs.get(Pair.with(inputMime, expectedMime));
        if (exec == null) {
            throw new RuntimeException("No conversion available for " + inputMime + " to " + expectedMime);
        }
        try {
            System.out.println("Executing: " + exec);
            Process process = Runtime.getRuntime().exec(exec);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Failed to convert JPEG to WebP, exit code: " + exitCode);
                throw new RuntimeException("Failed to convert JPEG to WebP, exit code: " + exitCode);
            } else {
                System.out.println("Successfully converted JPEG to WebP");
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //delete input file
        try {
            byte[] outputBytes = Files.readAllBytes(tempOutputFile);
            Files.deleteIfExists(tempInputFile);
            Files.deleteIfExists(tempOutputFile);
            return outputBytes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or delete temp files", e);
        }
    }
}
