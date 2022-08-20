package crypto.backend.springboot.minIO;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MinioInitializer {


    private String minioEndPoint;

    private String minioUserName;

    private String minioPassword;

    public MinioInitializer(@Value("${minio.endpoint}") String minioEndPoint, @Value("${minio.username}") String minioUserName, @Value("${minio.password}") String minioPassword) {
        this.minioEndPoint = minioEndPoint;
        this.minioUserName = minioUserName;
        this.minioPassword = minioPassword;
    }

    public static MinioClient getMinioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        return minioClient;
    }


}
