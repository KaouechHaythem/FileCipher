package com.example.Crypto.File;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
/**
 * This class is a service class responsible for encrypting and decrypting files
 * the protocol used is the following:
 * AES protocol to encrypt the file
 * RSA protocol to encrypt the secret key
 */
public class FileCryptoService {
    private final FileRepository fileRepository;
    private final CryptoRSAService crsa;

    @Autowired
    public FileCryptoService(FileRepository fileRepository, CryptoRSAService crsa) throws NoSuchAlgorithmException {
        this.fileRepository = fileRepository;
        this.crsa = crsa;

        this.crsa.init(1048);
    }

    public void init(int keySize) throws NoSuchAlgorithmException {

        crsa.init(keySize);

    }

    /**
     * any type of file encryption ...
     * note that we return the secret key in a byte[] format to be used later in the decryption
     * the process of the encryption is the following :
     * we encrypt the file using the AES amgorithm
     * we encrypt and return the secret key in a byte[] format
     *
     * @param algorithm
     * @param key
     * @param iv
     * @param inputFile
     * @param outputFile
     * @return
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */

    public byte[] encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                              java.io.File inputFile, java.io.File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
        return crsa.crypterByte(key.getEncoded());

    }

    /**
     * used to decrypt any kind of file
     * first , we decrypt the key given to us in the format of byte[] using the RSA Algorithm
     * then we decrypt the actual file using the AES algorithm
     *
     * @param algorithm
     * @param key
     * @param iv
     * @param inputFile
     * @param outputFile
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public void decryptFile(String algorithm, byte[] key, IvParameterSpec iv,
                            java.io.File inputFile, java.io.File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] decryptedSecretKeyByte = crsa.decrypterByte(key);
        SecretKeySpec decryptedSecretkey = new SecretKeySpec(decryptedSecretKeyByte, "AES");
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, decryptedSecretkey, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * a method used mainly for testing , we provide the name of the input , encrypted and output files
     * this method encrypts the inputFile and writes the result in encryptedFile
     * then decrypts encryptedFile and writes the result in outputFile
     *
     * @param inputFile
     * @param outputFile
     * @param encryptedFile
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public void fullEncryptionDecription(String inputFile, String outputFile, String encryptedFile) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        java.io.File input = new java.io.File(inputFile);
        java.io.File encrypted = new java.io.File(encryptedFile);
        java.io.File decrypted = new java.io.File(outputFile);

        init(1048);

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecretKey skey = kgen.generateKey();
        byte[] iv = new byte[128 / 8];

        IvParameterSpec ivspec = new IvParameterSpec(iv);
        String algo = "AES/CBC/PKCS5Padding";


        byte[] encryptedKey = encryptFile(algo, skey, ivspec, input, encrypted);
        decryptFile(algo, encryptedKey, ivspec, encrypted, decrypted);

    }
    //

    /**
     * this overwritten function gives you the possibility to specify the parameters like
     * the algorithm used and iv parameter
     *
     * @param algorithm
     * @param key
     * @param iv
     * @param inputFile
     * @param outputFile
     * @param encryptedFile
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public void fullEncryptionDecription(String algorithm, SecretKey key, IvParameterSpec iv,
                                         String inputFile, String outputFile, String encryptedFile) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        java.io.File input = new java.io.File(inputFile);
        java.io.File encrypted = new java.io.File(encryptedFile);
        java.io.File decrypted = new File(outputFile);
        byte[] encryptedKey = encryptFile(algorithm, key, iv, input, encrypted);
        decryptFile(algorithm, encryptedKey, iv, encrypted, decrypted);
    }

    /**
     * this method is used to Encrypt a MultipartFile and return the encrypted MultipartFile and the secret key as a parameter
     *
     * @param inputMultiFile
     * @param encryptedSkey  !!to be returned
     * @return MultipartFile
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public MultipartFile cryptUpload(MultipartFile inputMultiFile, byte[] encryptedSkey) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        init(1048);

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecretKey skey = kgen.generateKey();
        byte[] iv = new byte[128 / 8];

        IvParameterSpec ivspec = new IvParameterSpec(iv);
        String algo = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);


        File encrypted = new File("local" + inputMultiFile.getOriginalFilename());
        FileInputStream inputStream = (FileInputStream) inputMultiFile.getInputStream();


        FileOutputStream outputStream = new FileOutputStream(encrypted);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
        FileInputStream input = new FileInputStream(encrypted);
        MultipartFile outputFile = new MockMultipartFile(encrypted.getName(), inputMultiFile.getOriginalFilename(), "text/plain", IOUtils.toByteArray(input));

        input.close();
        encrypted.delete();
        encryptedSkey = crsa.crypterByte(skey.getEncoded());
        return outputFile;
    }

}
