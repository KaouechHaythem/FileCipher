package crypto.backend.springboot.client.service;

import crypto.backend.springboot.client.model.Client;
import crypto.backend.springboot.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findNameClient();
    }

    public Client findOne(String clientName) throws UserPrincipalNotFoundException {
        return clientRepository.findById(clientName).orElseThrow(() -> new UserPrincipalNotFoundException("Client not found"));
    }

    public void deleteClient(String clientName) {
        clientRepository.deleteById(clientName);
    }

    public void addClient(String clientName) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        if (!(clientRepository.existsById(clientName))) {


            KeyPair keyPair = generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            Client newClient = new Client(clientName, publicKey, privateKey);
            clientRepository.save(newClient);
        }

    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1048);
        return keyPairGen.generateKeyPair();
    }
}
