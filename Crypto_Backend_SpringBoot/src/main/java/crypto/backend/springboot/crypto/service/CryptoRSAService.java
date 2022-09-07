package crypto.backend.springboot.crypto.service;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

/**
 * used to encrypt/decrypt files using rsa algorithm
 */
@Component
public class CryptoRSAService {
    private KeyPair Key;
    private int keySize;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * used to initialize a CryptoRSAService instance by creating the keys and initializing the key size
     *
     * @param keySize
     * @throws NoSuchAlgorithmException
     */
    public void init(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(keySize);
        Key = keyPairGen.generateKeyPair();
        privateKey = Key.getPrivate();
        publicKey = Key.getPublic();

    }

    /**
     * used to encrypt a simple String message
     *
     * @param message
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String crypter(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] messageToBytes = message.getBytes();
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessage = c.doFinal(messageToBytes);
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    /**
     * used to decrypt a simple String message
     *
     * @param encryptedmessage
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public String decrypter(String encryptedmessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        byte[] messageToBytes = Base64.getDecoder().decode(encryptedmessage);

        Cipher c = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = c.doFinal(messageToBytes);

        return new String(decryptedMessage, "UTF8");

    }

    /**
     * used to encrypt something(object/file...) in the format of byte[] and return it in a byte[] format as well
     *
     * @param message
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] crypterByte(byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessage = c.doFinal(message);
        return encryptedMessage;
    }

    /**
     * used to decrypt something(object/file/message...) in the format of byte[]
     * and return the decrypted output in the same format
     *
     * @param encryptedmessage
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] decrypterByte(byte[] encryptedmessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {


        Cipher c = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = c.doFinal(encryptedmessage);

        return decryptedMessage;
    }

    public CryptoRSAService() {
    }
}
