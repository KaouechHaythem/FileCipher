package com.example.Crypto.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * used to test the local upload
 */
@RestController
@RequestMapping(path = "file")

public class FileController {
    public final FileCryptoService fileCryptoService;

    @Autowired
    public FileController(FileCryptoService fileCryptoService) {
        this.fileCryptoService = fileCryptoService;
    }

    @GetMapping
    public String crypter() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        fileCryptoService.fullEncryptionDecription("inputFile.txt", "outputFile.txt", "encryptedFile.txt");
        fileCryptoService.fullEncryptionDecription("inputFile.png", "outputFile.png", "encryptedFile.png");
        fileCryptoService.fullEncryptionDecription("inputFile.mp4", "outputFile.mp4", "encryptedFile.mp4");
        fileCryptoService.fullEncryptionDecription("inputFile.mp3", "outputFile.mp3", "encryptedFile.mp3");
        return "success";
    }
}
