package com.security.pki.certificate.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

@Service
public class KeyStoreService {

    private KeyStore keyStore;

    public void loadKeyStore(String filePath, char[] password) {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            File file = new File(filePath);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    keyStore.load(fis, password);
                }
            } else {
                keyStore.load(null, password);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading keystore: " + e.getMessage(), e);
        }
    }

    public void write(String alias, PrivateKey privateKey, char[] keyPassword, Certificate[] certificateChain) {
        try {
            keyStore.setKeyEntry(alias, privateKey, keyPassword, certificateChain);
        } catch (Exception e) {
            throw new RuntimeException("Error writing to keystore: " + e.getMessage(), e);
        }
    }

    public void saveKeyStore(String filePath, char[] password) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            keyStore.store(fos, password);
        } catch (Exception e) {
            throw new RuntimeException("Error saving keystore: " + e.getMessage(), e);
        }
    }

    public Resource generatePkcs12Resource(String alias, PrivateKey privateKey, char[] password, Certificate[] certificateChain) {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, password);
            keyStore.setKeyEntry(alias, privateKey, password, certificateChain);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                keyStore.store(bos, password);
                return new ByteArrayResource(bos.toByteArray());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PKCS#12 keystore: " + e.getMessage(), e);
        }
    }
}