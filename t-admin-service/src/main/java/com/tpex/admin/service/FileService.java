package com.tpex.admin.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Class FileService.
 * 
 */
@Service
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Inits the.
     * @throws IOException 
     */
    @PostConstruct
    public void init() throws IOException {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new IOException("Could not create upload folder!");
        }
    }
    /**
     * Save.
     *
     * @param file the file
     * @throws IOException 
     */
    public void save(MultipartFile file) throws IOException {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }

            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not store the file. Error: " + e.getMessage());
        }
    }
    /**
    * Delete all.
    *
    * @param file the file
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public void deleteAll(MultipartFile file) throws IOException {
    	String fileName = file.getOriginalFilename();
        Files.delete(Paths.get(fileName));

    }

   
}
