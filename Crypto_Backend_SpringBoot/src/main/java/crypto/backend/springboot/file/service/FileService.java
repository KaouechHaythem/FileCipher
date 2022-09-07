package crypto.backend.springboot.file.service;

import crypto.backend.springboot.minIO.MinioInitializer;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
/**
 * A service class responsible for uploading files
 */
public class FileService {

    // the file in wich the files will be uploaded locally
    @Value("${file.upload.folder}")
    private String uploadFolder;
    @Value("${download.folder}")
    private String downloadFolder;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private MinioInitializer minioInitializer;

    private FileCRUDService fileCRUDService;
    @Autowired
            private FileCryptoService fileCryptoService;

    HttpServletResponse res;
    public FileService(@Autowired MinioInitializer minioInitializer, @Autowired FileCRUDService fileCRUDService) {
        this.minioInitializer = minioInitializer;
        this.fileCRUDService = fileCRUDService;

    }

    MinioClient minioClient = MinioInitializer.getMinioClient();

    /**
     * this metod uploads a file to the specified uploadFolder
     *
     * @param file
     * @throws IOException
     */
    public void upload(MultipartFile file) throws IOException {
        byte[] data = file.getBytes();
        Path path = Paths.get(uploadFolder + file.getOriginalFilename());
        Files.write(path, data);

    }

    public void minIOUpload(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {

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
                    PutObjectArgs.builder().bucket(bucketName).object(file.getOriginalFilename()).stream(
                                    file.getInputStream(), -1, 10485760)
                            .build());

            System.out.println(
                    "'" + file.getOriginalFilename() + "' is successfully uploaded as "
                            + "object '" + file.getOriginalFilename() + "' to bucket " + bucketName + " .");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
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
     * upload file to minio and db with userName
     *
     * @param file
     * @param userName
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void minIODBUserUpload(MultipartFile file, String userName,boolean encrypted) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // save the file before sending
            // savedName is the name in wich the file will be stored in minIO
            // it s the combination of its uuid in the database and its originalname
            String savedName = fileCRUDService.addFile(file.getOriginalFilename(), userName,encrypted) + file.getOriginalFilename();
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
     * used to delete a file from a minio bucket and from the database
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
    public void removeFile(String uuid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucketName).object(uuid + fileCRUDService.deleteFile(uuid)).build());
    }

    public ResponseEntity<Resource> downloadFile(String fileId,String fileOriginName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String fileName = fileId+fileOriginName;
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
        File targetFile = new File(downloadFolder + fileName);

        if (fileCRUDService.getFile(fileId).isEncrypted()) {
            File tmpFile =new File(downloadFolder + "tmp" + fileName);
        java.nio.file.Files.copy(
                stream,
                tmpFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

            fileCryptoService.decryptStream(tmpFile, downloadFolder + fileName);
            tmpFile.delete();
        }
        else {
            java.nio.file.Files.copy(
                    stream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            
        }


        return   ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(targetFile.toPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new UrlResource(Paths.get(downloadFolder)
                        .resolve(targetFile.getName()).toUri()));




    }
    public void clearDownloadFolder(String fileName){
        File targetFile = new File(downloadFolder + fileName);
        targetFile.delete();
    }
    private byte[] contentOf(String fileName) throws URISyntaxException, IOException {
        return  Files.readAllBytes(Paths.get(fileName));
    }
}






