package com.example.Crypto.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
/**
 * this class provides an implementation of the CRUD of FILE
 */
public class FileService {
    @Autowired
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    /**
     * add a file to database
     * uuid is returned to be used in FileUploadService.addFile
     * @param fileName
     * @return
     */
    public UUID addFile(String fileName) {
        LocalDate dateOfCreation = LocalDate.now();
        File file = new File(fileName, dateOfCreation);
        fileRepository.save(file);
        return file.getId();
    }

    /**
     * delete a file from database
     * the original  name of the file is returned in order to be used in UploadService.removeFile
     * @param uuidAsString
     * @return
     */
    public String deleteFile(String uuidAsString) {
        UUID uuid = UUID.fromString(uuidAsString);
        String targetFileName = fileRepository.findById(uuid).get().getFileName();
        fileRepository.deleteById(uuid);
        return targetFileName;

    }
}
