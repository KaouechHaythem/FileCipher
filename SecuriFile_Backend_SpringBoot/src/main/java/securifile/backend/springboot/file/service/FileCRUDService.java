package securifile.backend.springboot.file.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import securifile.backend.springboot.file.model.File;
import securifile.backend.springboot.file.repository.FileRepository;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.util.List;

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

    /**
     * return all files
     *
     * @return
     */
    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    /**
     * return all files belonging to a cilent
     *
     * @param clientName
     * @return
     */
    public List<File> getFileByClient(String clientName) {
        return fileRepository.findByClientName(clientName);
    }

    /**
     * return one file by its uuid
     *
     * @param uuid
     * @return
     * @throws UserPrincipalNotFoundException
     */
    public File getFile(String uuid) throws UserPrincipalNotFoundException {

        return fileRepository.findById(uuid).orElseThrow(() -> new UserPrincipalNotFoundException("file not found"));
    }

    /**
     * add a file to database
     * uuid is returned to be used in FileUploadService.addFile
     *
     * @param fileName
     * @return
     */
    //this method is not actually used in the app it s only used by CryptoAESRSA
    public String addFile(String fileName) {
        LocalDate dateOfCreation = LocalDate.now();
        File file = new File(fileName, dateOfCreation);
        fileRepository.save(file);
        return file.getId();
    }

    /**
     * add a file to database
     * uuid is returned to be used in FileUploadService.addFile
     *
     * @param fileName
     * @return
     */
    public String addFile(String fileName, String userName, boolean encrypted) {
        LocalDate dateOfCreation = LocalDate.now();
        File file = new File(fileName, dateOfCreation, userName, encrypted);
        fileRepository.save(file);
        return file.getId();
    }

    /**
     * delete a file from database
     * the original  name of the file is returned in order to be used in UploadService.removeFile
     *
     * @param uuid
     * @return
     */
    public String deleteFile(String uuid) {

        String targetFileName = fileRepository.findById(uuid).get().getFileName();
        fileRepository.deleteById(uuid);
        return targetFileName;

    }


}
