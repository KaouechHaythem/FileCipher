package securifile.backend.springboot.keycloak;



import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import securifile.backend.springboot.client.model.Client;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
public class KeycloakService {

    @Autowired
    Keycloak keycloak;
    @Autowired
    KeycloakConfig keycloakConfig;

    /**
     * add a user to keycloak users
     * @param client
     */
    public void addUser(Client client) {
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(client.getPassWord());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(client.getClientName());

        userRepresentation.setCredentials(Collections.singletonList(credential));
        userRepresentation.setEnabled(true);
       

// set other required values


        keycloak.realm(keycloakConfig.getRealm()).users().create(userRepresentation);


    }

    /**
     * delete a user from keycloak
     * @param userName
     */
    public void deleteUser(String userName) {

        keycloak.realm(keycloakConfig.getRealm()).users().delete(getuserID(userName));

    }

    /**
     * return the userID from the userName
     * @param userName
     * @return
     */
    public  String getuserID(String userName) {
        try {
            return keycloak.realm(keycloakConfig.getRealm()).users().search(userName).get(0).getId();
        }
        catch (Exception e){
            return "";
        }
    }
}
