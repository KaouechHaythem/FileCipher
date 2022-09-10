package securifile.backend.springboot.client.controller;

import securifile.backend.springboot.client.service.ClientService;
import securifile.backend.springboot.client.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "findall")
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping(path = "addclient/{clientname}")
    public void addClient(@PathVariable("clientname") String clientName) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        clientService.addClient(clientName);
    }
    @DeleteMapping(path = "delete/{clientname}")
    public void deleteClient(@PathVariable("clientname")String clientName){
        clientService.deleteClient(clientName);
    }
}
