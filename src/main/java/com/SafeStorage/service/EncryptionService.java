package com.SafeStorage.service;

import com.SafeStorage.dto.ChangePasswordDto;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

@Service
public class EncryptionService {

    public static SecretKey generateSecretKey(String password, byte [] iv) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), iv, 65536, 128);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public byte [] encrypt(String password, byte [] data) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] nonce  = new byte[12];
        secureRandom.nextBytes(nonce );

        SecretKey secretKey = generateSecretKey(password, nonce );
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce );

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte [] encryptedData = cipher.doFinal(data);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + nonce .length + encryptedData.length);
        byteBuffer.putInt(nonce .length);
        byteBuffer.put(nonce );
        byteBuffer.put(encryptedData);

        return byteBuffer.array();
    }

    public byte [] decrypt(String password, byte [] encryptedData) throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        int nonceSize = byteBuffer.getInt();
        if(nonceSize < 12 || nonceSize >= 16) {
            throw new IllegalArgumentException("Nonce incorrect");
        }

        byte[] nonce = new byte[nonceSize];
        byteBuffer.get(nonce);
        SecretKey secretKey = generateSecretKey(password, nonce);
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        return cipher.doFinal(cipherBytes);
    }


    public byte [] changePassword(byte [] encryptedData, ChangePasswordDto changePasswordDto) throws Exception {
        return encrypt(changePasswordDto.getNewPassword(), decrypt(changePasswordDto.getOldPassword(),encryptedData));
    }

}
