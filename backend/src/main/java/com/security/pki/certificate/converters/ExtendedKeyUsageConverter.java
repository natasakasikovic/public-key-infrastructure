package com.security.pki.certificate.converters;

import org.bouncycastle.asn1.x509.KeyPurposeId;

import java.util.ArrayList;
import java.util.List;

public class ExtendedKeyUsageConverter {

    public static KeyPurposeId[] convertToExtendedKeyUsages(List<String> extendedKeyUsages) {
        List<KeyPurposeId> purposes = new ArrayList<>();

        for (String eku : extendedKeyUsages) {
            switch (eku) {
                case "TSL Web Server Authentication":
                    purposes.add(KeyPurposeId.id_kp_serverAuth);
                    break;
                case "TLS Web Client Authentication":
                    purposes.add(KeyPurposeId.id_kp_clientAuth);
                    break;
                case "Sign Executable Code":
                    purposes.add(KeyPurposeId.id_kp_codeSigning);
                    break;
                case "Email Protection":
                    purposes.add(KeyPurposeId.id_kp_emailProtection);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown extended key usage: " + eku);
            }
        }

        return purposes.toArray(new KeyPurposeId[0]);
    }
}