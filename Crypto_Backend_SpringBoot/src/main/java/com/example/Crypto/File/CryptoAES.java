package com.example.Crypto.File;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

/**
 * used to decrypt and encrypt files using the  AES algorithm
 */
public class CryptoAES {
    /**
     * used to generate a Secret key
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SecretKey keyGen() throws NoSuchAlgorithmException {

        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecretKey skey = kgen.generateKey();
        return skey;
    }

    /**
     * used to generate ivspec
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public IvParameterSpec ivGen() throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] iv = new byte[128 / 8];
        SecureRandom srandom = SecureRandom.getInstance("SHA1PRNG", "SUN");

        IvParameterSpec ivspec = new IvParameterSpec(iv);
        return ivspec;
    }

    public SecretKey skey = keyGen();
    public IvParameterSpec ivspec = ivGen();

    /**
     * used to encrypt a simple String message
     *
     * @param message
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {


        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
        byte[] input = message.getBytes("UTF-8");
        byte[] encoded = ci.doFinal(input);
        return Base64.getEncoder().encodeToString(encoded);

    }

    /**
     * used to decrypt a simple String message
     */

    public String decrypt(String encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        byte[] messageToBytes = Base64.getDecoder().decode(encryptedMessage);
        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, skey, ivspec);
        byte[] decryptedMessage = ci.doFinal(messageToBytes);

        return new String(decryptedMessage, "UTF8");

    }

    /**
     * used to encrypt a simple String message after specifying the secret key and the ivspec
     *
     * @param message
     * @param secretKey
     * @param ivspec
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encryptArg(String message, SecretKey secretKey, IvParameterSpec ivspec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {


        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        byte[] input = message.getBytes("UTF-8");
        byte[] encoded = ci.doFinal(input);
        return Base64.getEncoder().encodeToString(encoded);

    }

    /**
     * used to decrypt a simple String message after specifying the secret key and the ivspec
     *
     * @param encryptedMessage
     * @param secretKey
     * @param ivspec
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decryptArg(String encryptedMessage, SecretKey secretKey, IvParameterSpec ivspec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        byte[] messageToBytes = Base64.getDecoder().decode(encryptedMessage);
        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        byte[] decryptedMessage = ci.doFinal(messageToBytes);

        return new String(decryptedMessage, "UTF8");

    }


    public CryptoAES() throws NoSuchAlgorithmException, NoSuchProviderException {
    }
}
