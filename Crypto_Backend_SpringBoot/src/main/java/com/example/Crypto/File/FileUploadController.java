
package com.example.Crypto.File;


import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Text;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * A controller class responsible for uploading the files
 */
@RestController
@RequestMapping("upload")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileCryptoService fileCryptoService;
    @Autowired
    private FileService fileService;

    /**
     * upload the files , locally , without encrypting them
     *
     * @param multipartFile
     * @throws IOException
     */
    @PostMapping(path = "normal")
    public void upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        fileUploadService.upload(multipartFile);

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
    @PostMapping(path = "crypto")
    public void cryptoUpload(@RequestParam("file") MultipartFile multipartFile) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        byte[] skey = new byte[128];
        fileUploadService.upload(fileCryptoService.cryptUpload(multipartFile, skey));
    }

    /**
     * upload the files to minIO without encrypting them
     *
     * @param multipartFile
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @PostMapping(path = "minio")
    public void minIOUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        fileUploadService.minIOUpload(multipartFile, "normal");

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
    @PostMapping(path = "cryptominio")

    public void cryptoMinioUpload(@RequestParam("file") MultipartFile multipartFile) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        byte[] skey = new byte[128];

        fileUploadService.minIOUpload(fileCryptoService.cryptUpload(multipartFile, skey), "crypto");


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
    @PostMapping(path = "cryptominiodb")
    public void cryptoMinioDBUpload(@RequestParam("file") MultipartFile multipartFile) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        byte[] skey = new byte[128];

        fileUploadService.minIODBUpload(fileCryptoService.cryptUpload(multipartFile, skey), "crypto");


    }

    /**
     * used to delete a file from minIO and From Database
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
    @PostMapping("deletefile")
    public void removeFile(@RequestParam("uuid") String uuid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileUploadService.removeFile(uuid.toString(),"crypto");
    }
}
