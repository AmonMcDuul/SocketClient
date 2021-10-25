package com.socket_client;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUpload {
    public String fileName;
    public Long contentLength;
    public String checksum;

    public FileUpload(java.io.File file) {
        fileName = file.getName();
        contentLength = file.length();
        checksum = getChecksum(file);
    }

    /**
     * Get the checksum of a file
     * @param file
     * @return
     */
    private String getChecksum(java.io.File file) {
        byte[] bytes;
        String checksum = null;
        try {
            bytes = Files.readAllBytes(Paths.get(file.getPath()));
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            checksum = DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }
}

