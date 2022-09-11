package notused;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import securifile.backend.springboot.file.service.FileCRUDService;
import securifile.backend.springboot.file.service.FileCryptoService;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoAESRSA {
    //the files in which the files will be uploaded locally
    String uploadFolder = "";
    String bucketName = "";
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    FileCRUDService fileCRUDService;
    FileCryptoService fileCryptoService;
    private final CryptoRSA crsa;

    public CryptoAESRSA(CryptoRSA crsa) {
        this.crsa = crsa;
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

        crsa.init(1048);

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecretKey skey = kgen.generateKey();
        byte[] iv = new byte[128 / 8];

        IvParameterSpec ivspec = new IvParameterSpec(iv);
        String algo = "AES/CBC/PKCS5Padding";


        byte[] encryptedKey = encryptFile(algo, skey, ivspec, input, encrypted);
        decryptFile(algo, encryptedKey, ivspec, encrypted, decrypted);

    }

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
     * used to upload a file to minio encrypt the secret key and return it as a parameter
     *
     * @param inputMultiFile
     * @param encryptedSkey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public MultipartFile cryptUpload(MultipartFile inputMultiFile, byte[] encryptedSkey) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        crsa.init(1048);

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

    /**
     * return the contents of a File
     *
     * @param fileName
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private byte[] contentOf(String fileName) throws URISyntaxException, IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    /**
     * generate a secret key and encrypt it
     *
     * @param publicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public String generateSecretKey(PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecretKey skey = kgen.generateKey();
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = c.doFinal(skey.getEncoded());


        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    /**
     * transform privateKey from string to PrivateKey format
     *
     * @param strPrivateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PrivateKey transformPrivateKey(String strPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec priavteKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(strPrivateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(priavteKeySpec);

    }

    /**
     * transform publicKey from string to PublicKey format
     *
     * @param strPublicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PublicKey transformPUblicKey(String strPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(strPublicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);

    }

    /**
     * upload the files , locally , without encrypting them
     *
     * @param multipartFile
     * @throws IOException
     */
    @PostMapping(path = "normalupload")
    public void upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        uploadService(multipartFile);

    }

    /**
     * this metod uploads a file to the specified uploadFolder
     *
     * @param file
     * @throws IOException
     */
    public void uploadService(MultipartFile file) throws IOException {
        byte[] data = file.getBytes();

        Path path = Paths.get(uploadFolder + file.getOriginalFilename());
        Files.write(path, data);

    }


    /**
     * used to upload an encrypted file to aminio server and to the database
     *
     * @param file
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void minIODBUpload(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // save the file before sending
            // savedName is the name in wich the file will be stored in minIO
            // it s the combination of its uuid in the database and its originalname

            String savedName = fileCRUDService.addFile(file.getOriginalFilename()) + file.getOriginalFilename();
            // Create a minioClient with the local MinIO server , its access key and secret key.

            // Make 'crypto' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'crypto'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket " + bucketName + " already exists.");
            }
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(savedName).stream(
                                    file.getInputStream(), -1, 10485760)
                            .build());

            System.out.println(
                    "'" + file.getOriginalFilename() + "' is successfully uploaded as "
                            + "object '" + savedName + "' to bucket " + bucketName + " .");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
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
        upload(cryptUpload(multipartFile, skey));
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
        minIOUpload(multipartFile);

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


        minIOUpload(cryptUpload(multipartFile, skey));


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

            minIODBUpload(cryptUpload(multipartFile, skey));
        } else {
            minIODBUpload(multipartFile);
        }

    }


}
