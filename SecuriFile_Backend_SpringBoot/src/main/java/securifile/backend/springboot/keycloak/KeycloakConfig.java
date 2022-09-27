package securifile.backend.springboot.keycloak;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {


    // @Value("${keycloak.server.url}")
    public  String serverUrl ="http://localhost:8100/";
    //   @Value("${keycloak.realm}")
    private   String realm ="SecuriFile";
    //   @Value("${keycloak.cilent.id}")
    static String clientId ="springboot-client" ;
    //    @Value("${keycloak.cilent.secret}")
    static String clientSecret = "Q4o466miSa9rxwuIQvD5Pb5wPFYDI5XS";
    //   @Value("${keycloak.admin.username}")
    static String userName ="manager" ;
    //  @Value("${keycloak.admin.password}")
    static String password ="manager";
    public KeycloakConfig() {
    }

@Bean
    public  Keycloak getInstance(){


           Keycloak keycloak=KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build()).build();


        return keycloak;
    }

    public  String getRealm() {
        return realm;
    }


}
