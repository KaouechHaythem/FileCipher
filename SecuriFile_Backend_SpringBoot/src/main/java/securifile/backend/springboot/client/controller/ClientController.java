package securifile.backend.springboot.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import securifile.backend.springboot.client.model.Client;
import securifile.backend.springboot.client.service.ClientService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("api/v1/client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    /**
     * return all cilents
     * @return
     */
    @GetMapping(path = "findall")
    public List<Client> findAll() {
        return clientService.findAll();
    }

    /**
     * add a client
     * @param clientName
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    @GetMapping(path = "addclient/{clientname}")
    public void addClient(@PathVariable("clientname") String clientName) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        clientService.addClient(clientName);
    }

    /**
     * delete a client
     * @param clientName
     */
    @DeleteMapping(path = "delete/{clientname}")
    public void deleteClient(@PathVariable("clientname") String clientName) {
        clientService.deleteClient(clientName);
    }
}
