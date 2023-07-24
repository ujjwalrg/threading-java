package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.exception.CipherException;
import com.mewebstudio.javaspringbootboilerplate.util.AESCipher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CipherService {
    @Value("${app.secret}")
    private String appSecret;

    /**
     * Encrypt plain text with secret key.
     *
     * @param plainText String
     * @param secretKey String (256 bit)
     * @return String
     * @throws RuntimeException Encrypting exception
     */
    public String encrypt(String plainText, String secretKey) {
        try {
            return AESCipher.encrypt(plainText, secretKey);
        } catch (Exception e) {
            log.error("Encrypting error", e);
            throw new CipherException(e);
        }
    }

    /**
     * Encrypt plain text with app secret.
     *
     * @param plainText String
     * @return String
     * @throws RuntimeException Encrypting exception
     */
    public String encrypt(String plainText) {
        try {
            return encrypt(plainText, appSecret);
        } catch (Exception e) {
            log.error("Encrypting error", e);
            throw new CipherException(e);
        }
    }

    /**
     * Decrypt cipher text with secret key.
     *
     * @param encryptedText String
     * @param secretKey     String (256 bit)
     * @return String
     * @throws RuntimeException Decrypting exception
     */
    public String decrypt(String encryptedText, String secretKey) {
        try {
            return AESCipher.decrypt(encryptedText, secretKey);
        } catch (Exception e) {
            log.error("Decrypting error", e);
            throw new CipherException(e);
        }
    }

    /**
     * Decrypt cipher text with app secret.
     *
     * @param encryptedText String
     * @return String
     * @throws RuntimeException Decrypting exception
     */
    public String decrypt(String encryptedText) {
        try {
            return decrypt(encryptedText, appSecret);
        } catch (Exception e) {
            log.error("Decrypting error", e);
            throw new CipherException(e);
        }
    }
}