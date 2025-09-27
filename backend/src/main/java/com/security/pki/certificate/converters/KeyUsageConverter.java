package com.security.pki.certificate.converters;

import java.util.List;
import org.bouncycastle.asn1.x509.KeyUsage;

public class KeyUsageConverter {

    public static int convertKeyUsageToInt(List<String> keyUsages) {
        int usage = 0;

        for (String ku : keyUsages) {
            switch (ku) {
                case "Digital Signature":
                    usage |= KeyUsage.digitalSignature;
                    break;
                case "Non Repudiation":
                    usage |= KeyUsage.nonRepudiation;
                    break;
                case "Key Encipherment":
                    usage |= KeyUsage.keyEncipherment;
                    break;
                case "Data Encipherment":
                    usage |= KeyUsage.dataEncipherment;
                    break;
                case "Key Agreement":
                    usage |= KeyUsage.keyAgreement;
                    break;
                case "Key Cert Sign":
                    usage |= KeyUsage.keyCertSign;
                    break;
                case "CRL Sign":
                    usage |= KeyUsage.cRLSign;
                    break;
                case "Encipher Only":
                    usage |= KeyUsage.encipherOnly;
                    break;
                case "Decipher Only":
                    usage |= KeyUsage.decipherOnly;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key usage: " + ku);
            }
        }
        return usage;
    }
}