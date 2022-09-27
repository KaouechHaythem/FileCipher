package securifile.backend.springboot.minIO;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
/**
 * this component is used to initialize a MinioClient
 */
public class MinioInitializer {

    @Value("${minio.endpoint}")
    private String minioEndPoint;
    @Value("${minio.username}")
    private String minioUserName;
    @Value("${minio.password}")
    private String minioPassword;

    public MinioInitializer( ) {

    }

    /**
     * initialize MinioClient
     * @return
     */
    @Bean
    public  MinioClient getMinioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioEndPoint)
                        .credentials(minioUserName, minioPassword)
                        .build();
        return minioClient;
    }


}
