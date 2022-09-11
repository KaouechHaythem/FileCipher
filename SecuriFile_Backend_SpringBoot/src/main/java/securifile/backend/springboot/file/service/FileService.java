package securifile.backend.springboot.file.service;

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
import securifile.backend.springboot.minIO.MinioInitializer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
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

    // the file in wich the files will be downloaded locally before downloading them in the front

    @Value("${download.folder}")
    private String downloadFolder;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private MinioInitializer minioInitializer;

    private FileCRUDService fileCRUDService;
    @Autowired
    private FileCryptoService fileCryptoService;


    public FileService(@Autowired MinioInitializer minioInitializer, @Autowired FileCRUDService fileCRUDService) {
        this.minioInitializer = minioInitializer;
        this.fileCRUDService = fileCRUDService;

    }

    MinioClient minioClient = MinioInitializer.getMinioClient();


    /**
     * upload file to minio and db with userName
     *
     * @param file
     * @param userName
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void minIODBUserUpload(MultipartFile file, String userName, boolean encrypted) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // save the file before sending
            // savedName is the name in wich the file will be stored in minIO
            // it s the combination of its uuid in the database and its originalname
            String savedName = fileCRUDService.addFile(file.getOriginalFilename(), userName, encrypted) + file.getOriginalFilename();
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

    /**
     * used to download a file from minio to a download folder
     * then sent to the front to be downloaded with httpservlet
     *
     * @param fileId
     * @param fileOriginName
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
     * @throws URISyntaxException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */

    public ResponseEntity<Resource> downloadFile(String fileId, String fileOriginName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, URISyntaxException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String fileName = fileId + fileOriginName;
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
        File targetFile = new File(downloadFolder + fileName);

        if (fileCRUDService.getFile(fileId).isEncrypted()) {
            File tmpFile = new File(downloadFolder + "tmp" + fileName);
            java.nio.file.Files.copy(
                    stream,
                    tmpFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            fileCryptoService.decryptStream(tmpFile, downloadFolder + fileName);
            tmpFile.delete();
        } else {
            java.nio.file.Files.copy(
                    stream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

        }


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(targetFile.toPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new UrlResource(Paths.get(downloadFolder)
                        .resolve(targetFile.getName()).toUri()));


    }

    /**
     * clear the download folder after downloading a file
     * this method must be called from the front after calling the download method
     *
     * @param fileName
     */
    public void clearDownloadFolder(String fileName) {
        File targetFile = new File(downloadFolder + fileName);
        targetFile.delete();
    }

}






