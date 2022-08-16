package com.example.Crypto.File;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
/**
 * A service class responsible for uploading files
 */
public class FileUploadService {
    private String minioEndPoint="http://127.0.0.1:9000";
    private String minioUserName ="minioadmin";
    private String minioPassword ="minioadmin";
    // the file in wich the files will be uploaded locally
    private String uploadFolder = "C:/Users/ASUS/Desktop/Work/Advyteam/Crypto/uploads/id";
    @Autowired
    private FileService fileService;

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

    public void minIOUpload(MultipartFile file, String bucketName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {

            // Create a minioClient with the local MinIO server , its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minioEndPoint)
                            .credentials(minioUserName, minioPassword)
                            .build();

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
     * @param file
     * @param bucketName
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void minIODBUpload(MultipartFile file, String bucketName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // save the file before sending
            // savedName is the name in wich the file will be stored in minIO
            // it s the combination of its uuid in the database and its originalname
            String savedName = fileService.addFile(file.getOriginalFilename()).toString() + file.getOriginalFilename();
            // Create a minioClient with the local MinIO server , its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minioEndPoint)
                            .credentials(minioUserName, minioPassword)
                            .build();

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
     * @param uuid
     * @param bucketName
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
    public void removeFile(String uuid,String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioEndPoint)
                        .credentials(minioUserName, minioPassword)
                        .build();
        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucketName).object(uuid+fileService.deleteFile(uuid)).build());
    }
    }






