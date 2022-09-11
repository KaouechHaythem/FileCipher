
package securifile.backend.springboot.file.controller;


import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import securifile.backend.springboot.file.model.File;
import securifile.backend.springboot.file.service.FileCRUDService;
import securifile.backend.springboot.file.service.FileCryptoService;
import securifile.backend.springboot.file.service.FileService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * A controller class responsible for uploading the files
 */
@RestController
@RequestMapping("api/v1/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private FileCryptoService fileCryptoService;
    @Autowired
    private FileCRUDService fileCRUDService;

    /**
     * return all th files in the database
     *
     * @return
     */
    @GetMapping(path = "findall")
    public List<File> getAll() {
        return fileCRUDService.getFiles();
    }

    /**
     * return all files belonging to specified client
     *
     * @param clientName
     * @return
     */
    @GetMapping(path = "findbyclient/{clientname}")
    public List<File> getByClient(@PathVariable("clientname") String clientName) {
        return fileCRUDService.getFileByClient(clientName);
    }

    /**
     * return one file by its id
     *
     * @param uuid
     * @return
     */
    @GetMapping("findone/{uuid}")
    public File getOne(@PathVariable("uuid") String uuid) throws UserPrincipalNotFoundException {
        return fileCRUDService.getFile(uuid);
    }

    /**
     * Upload a file to minio and Database after chosing wether to encrypt it or not
     *
     * @param multipartFile
     * @param clientName
     * @param crypto
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    @PostMapping("finalupload/{clientname}/{crypto}")
    public void uploadFileFinal(@RequestParam("file") MultipartFile multipartFile,
                                @PathVariable("clientname") String clientName,
                                @PathVariable("crypto") boolean crypto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

        if (crypto) {
            fileService.minIODBUserUpload(fileCryptoService.cryptUpload(multipartFile), clientName, crypto);
        } else {
            fileService.minIODBUserUpload(multipartFile, clientName, crypto);
        }
    }

    /**
     * Download a file from minio toa chosen folder
     *
     * @param fileName
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @GetMapping("download/{fileid}/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String fileName, @PathVariable("fileid") String fileid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        return fileService.downloadFile(fileid, fileName);
    }


    /**
     * clear the download folder after downloading a file
     * this method must be called from the front after calling the download method
     *
     * @param filename
     */
    @DeleteMapping("clear/{filename}")
    public void clearDownloadFolder(@PathVariable("filename") String filename) {
        fileService.clearDownloadFolder(filename);
    }

    /**
     * used to delete a file from minIO and From Database
     *
     * @param uuid
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @DeleteMapping("delete/{uuid}")
    public void removeFile(@PathVariable("uuid") String uuid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.removeFile(uuid.toString());
    }


}
