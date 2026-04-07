package com.carbo.job.utils;

import org.springframework.web.multipart.MultipartFile;

public class FileOperation{
    public static String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}