
package securifile.backend.springboot.file.controller;


import securifile.backend.springboot.file.model.File;
import securifile.backend.springboot.file.service.FileCRUDService;
import securifile.backend.springboot.file.service.FileCryptoService;
import securifile.backend.springboot.file.service.FileService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * upload the files , locally , without encrypting them
     *
     * @param multipartFile
     * @throws IOException
     */
    @PostMapping(path = "normalupload")
    public void upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        fileService.upload(multipartFile);

    }

    /**
     * Encrypting the files then uploading them locally
     *
     * @param multipartFile
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @PostMapping(path = "cryptoupload")
    public void cryptoUpload(@RequestParam("file") MultipartFile multipartFile) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        byte[] skey = new byte[128];
        fileService.upload(fileCryptoService.cryptUpload(multipartFile, skey));
    }

    /**
     * upload the files to minIO without encrypting them
     *
     * @param multipartFile
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @PostMapping(path = "minioupload")
    public void minIOUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        fileService.minIOUpload(multipartFile);

    }

    /**
     * Encrypting the files then uploading them to minIO
     *
     * @param multipartFile
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @PostMapping(path = "cryptominioupload")

    public void cryptoMinioUpload(@RequestParam("file") MultipartFile multipartFile) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        byte[] skey = new byte[128];

        fileService.minIOUpload(fileCryptoService.cryptUpload(multipartFile, skey));


    }

    /**
     * Encrypting the files then uploading them to minIO and save their trace to database
     *
     * @param multipartFile
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    @PostMapping(path = "miniodbupload/{crypto}")
    public void cryptoMinioDBUpload(@RequestParam("file") MultipartFile multipartFile, @PathVariable("crypto") boolean crypto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        if (crypto) {
            byte[] skey = new byte[128];

            fileService.minIODBUpload(fileCryptoService.cryptUpload(multipartFile, skey));
        } else {
            fileService.minIODBUpload(multipartFile);
        }

    }
    @PostMapping(path = "userfileupload/{crypto}")
    public void cryptoMinioDBUserUpload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("clientname")String clientName, @PathVariable("crypto") boolean crypto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        if (crypto) {
            byte[] skey = new byte[128];

            fileService.minIODBUserUpload(fileCryptoService.cryptUpload(multipartFile, skey),clientName,crypto);
        } else {
            fileService.minIODBUserUpload(multipartFile,clientName,crypto);
        }

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

    /**
     * return all th files in the database
     *
     * @return
     */
    @GetMapping(path = "findall")
    public List<File> getAll() {
        return fileCRUDService.getFiles();
    }

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
     * Download a file from minio toa chosen folder
     * @param fileName
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     * @return
     */
    @GetMapping("download/{fileid}/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename")String fileName ,@PathVariable("fileid") String fileid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
       return fileService.downloadFile(fileid,fileName);
    }
    @PostMapping("finalupload/{clientname}/{crypto}")
    public void uploadFileFinal(@RequestParam("file")MultipartFile multipartFile,
                                  @PathVariable("clientname")String clientName,
                                @PathVariable("crypto")boolean crypto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {

    if (crypto){
        fileService.minIODBUserUpload(fileCryptoService.cryptUpload(multipartFile,clientName),clientName,crypto);
    }
    else{
        fileService.minIODBUserUpload(multipartFile,clientName,crypto);
    }
    }
    @DeleteMapping("clear/{filename}")
    public void clearDownloadFolder(@PathVariable("filename") String filename){
        fileService.clearDownloadFolder(filename);
    }

}
