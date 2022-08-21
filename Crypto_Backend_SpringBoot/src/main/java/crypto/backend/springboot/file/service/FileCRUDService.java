package crypto.backend.springboot.file.service;

import crypto.backend.springboot.file.model.File;
import crypto.backend.springboot.file.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
/**
 * this class provides an implementation of the CRUD of FILE
 */
public class FileCRUDService {
    @Autowired
    private final FileRepository fileRepository;

    public FileCRUDService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public List<File> getFiles() {
        return fileRepository.findAll();
    }
    public File getFile(String uuid) throws UserPrincipalNotFoundException {
        UUID id = UUID.fromString(uuid);
        return fileRepository.findById(id).orElseThrow(()->new UserPrincipalNotFoundException("file not found"));
    }
    /**
     * add a file to database
     * uuid is returned to be used in FileUploadService.addFile
     *
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
     *
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
