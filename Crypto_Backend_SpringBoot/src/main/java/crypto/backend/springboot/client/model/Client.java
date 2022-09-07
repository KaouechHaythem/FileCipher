package crypto.backend.springboot.client.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Client {


    @Id


    private String clientName;
    @Column(length = 2048)
    private String publicKey;
    @Column(length = 2048)
    private String privateKey;


    public Client() {
    }

    public Client(String clientName) {
        this.clientName = clientName;
    }

    public Client(String userName, String publicKey, String privateKey) {
        this.clientName = userName;
        this.publicKey = publicKey;
        this.privateKey = privateKey;

    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }


    @Override
    public String toString() {
        return "User{" +
                "userName='" + clientName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
