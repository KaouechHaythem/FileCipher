package securifile.backend.springboot.file.service;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import securifile.backend.springboot.client.service.ClientService;
import securifile.backend.springboot.file.repository.FileRepository;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


@Service
/**
 * This class is a service class responsible for encrypting and decrypting files
 * the protocol used is the following:
 * AES protocol to encrypt the file
 * RSA protocol to encrypt the secret key
 */
public class FileCryptoService {
    @Value("${secret.key}")
    String strsecretKey;
    KeyGenerator kgen = KeyGenerator.getInstance("AES");


    private final FileRepository fileRepository;

    private final IvParameterSpec ivspec = new IvParameterSpec(new byte[128 / 8]);
    HttpServletResponse res;

    public IvParameterSpec getIvspec() {
        return ivspec;
    }

    @Autowired
    ClientService clientService;

    @Autowired
    public FileCryptoService(FileRepository fileRepository) throws NoSuchAlgorithmException {
        this.fileRepository = fileRepository;

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


    /**
     * used to encrypt a MiltipartFile using aes algorithm
     *
     * @param inputMultiFile
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     */

    public MultipartFile cryptUpload(MultipartFile inputMultiFile) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {


        byte[] decodedSecretKey = Base64.getDecoder().decode(strsecretKey);


        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, "AES");
        String algo = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);


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


        return outputFile;
    }

    public void decryptStream(File inputFile, String fileName) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {


        System.out.println("secret key " + strsecretKey);


        byte[] decodedSecretKey = Base64.getDecoder().decode(strsecretKey);


        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, "AES");
        String algo = "AES/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(fileName);
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


}
