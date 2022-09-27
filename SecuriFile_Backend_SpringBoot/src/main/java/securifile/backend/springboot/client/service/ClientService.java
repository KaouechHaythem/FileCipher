package securifile.backend.springboot.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import securifile.backend.springboot.client.model.Client;
import securifile.backend.springboot.client.repository.ClientRepository;
import securifile.backend.springboot.keycloak.KeycloakService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

@Service
public class ClientService {
    @Autowired
    public final ClientRepository clientRepository;
    @Autowired
    private KeycloakService keycloakService;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * return all Clients(only their names)
     *
     * @return
     */
    public List<Client> findAll() {
        return clientRepository.findNameClient();
    }

    /**
     * return one client
     *
     * @param clientName
     * @return
     * @throws UserPrincipalNotFoundException
     */
    public Client findOne(String clientName) throws UserPrincipalNotFoundException {
        return clientRepository.findById(clientName).orElseThrow(() -> new UserPrincipalNotFoundException("Client not found"));
    }

    /**
     * delete a client
     *
     * @param clientName
     */
    public void deleteClient(String clientName) {

        keycloakService.deleteUser(clientName);
        clientRepository.deleteById(clientName);
    }

    /**
     * add a client
     *
     * @param clientName
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public void addClient(String clientName,String clientPass) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (!(clientRepository.existsById(clientName))) {


            KeyPair keyPair = generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            Client newClient = new Client(clientName, clientPass, publicKey, privateKey);
            keycloakService.addUser(newClient);

            clientRepository.save(newClient);

        }

    }

    /**
     * generate public key and private key
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1048);
        return keyPairGen.generateKeyPair();
    }
}
