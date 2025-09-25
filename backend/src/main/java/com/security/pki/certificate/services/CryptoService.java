package com.security.pki.certificate.services;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.*;

@Service
public class CryptoService {
    private static final SecureRandom RANDOM = new SecureRandom();

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

    public byte[] wrapKeyWithRsa(PublicKey publicKey, SecretKey dek) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        return cipher.wrap(dek);
    }

    public SecretKey unwrapKeyWithRsa(PrivateKey privateKey, byte[] wrappedDek) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        return (SecretKey) cipher.unwrap(wrappedDek, "AES", Cipher.SECRET_KEY);
    }

    public SecretKey generateDek() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

}