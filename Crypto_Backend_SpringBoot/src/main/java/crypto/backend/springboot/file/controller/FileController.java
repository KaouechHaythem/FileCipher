
package crypto.backend.springboot.file.controller;


import crypto.backend.springboot.file.model.File;
import crypto.backend.springboot.file.service.FileCRUDService;
import crypto.backend.springboot.file.service.FileCryptoService;
import crypto.backend.springboot.file.service.FileService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

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
    @DeleteMapping("{uuid}")
    public void removeFile(@PathVariable("uuid") String uuid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.removeFile(uuid.toString());
    }

    @GetMapping(path = "getall")
    public List<File> getAll() {
        return fileCRUDService.getFiles();
    }

    @GetMapping("{uuid}")
    public Optional<File> getOne(@PathVariable("uuid") String uuid) {
        return fileCRUDService.getFile(uuid);
    }
}
