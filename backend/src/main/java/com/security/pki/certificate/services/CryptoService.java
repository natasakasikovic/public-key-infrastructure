package com.security.pki.certificate.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoService {
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${app.master-key}")
    private String masterPublicKeyBase64;

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(2048, random);
        return keyGen.generateKeyPair();
    }

    public byte[] encrypt(SecretKey dek, byte[] plaintext) throws GeneralSecurityException {
        byte[] iv = new byte[12];
        RANDOM.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, dek, spec);
        byte[] ct = cipher.doFinal(plaintext);
        ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
        buf.put(iv);
        buf.put(ct);
        return buf.array();
    }

    public SecretKey loadMasterKey() {
        byte[] decoded = Base64.getDecoder().decode(masterPublicKeyBase64);
        return new SecretKeySpec(decoded, "AES");
    }

    public byte[] decrypt(SecretKey dek, byte[] ivPlusCiphertext) throws GeneralSecurityException {
        ByteBuffer buf = ByteBuffer.wrap(ivPlusCiphertext);
        byte[] iv = new byte[12];
        buf.get(iv);
        byte[] ct = new byte[buf.remaining()];
        buf.get(ct);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, dek, spec);
        return cipher.doFinal(ct);
    }

    public byte[] wrapDek(SecretKey masterKey, SecretKey dek) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AESWrap", "BC");
        cipher.init(Cipher.WRAP_MODE, masterKey);
        return cipher.wrap(dek);
    }

    public SecretKey unwrapDek(SecretKey masterKey, byte[] wrappedDek) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AESWrap", "BC");
        cipher.init(Cipher.UNWRAP_MODE, masterKey);
        return (SecretKey) cipher.unwrap(wrappedDek, "AES", Cipher.SECRET_KEY);
    }

    public SecretKey generateDek() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

}