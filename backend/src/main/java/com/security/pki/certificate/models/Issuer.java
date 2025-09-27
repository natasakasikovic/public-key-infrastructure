package com.security.pki.certificate.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;


@Getter
@Setter
@AllArgsConstructor
@Embeddable
@NoArgsConstructor
public class Issuer {
    private String principalName;

    public Issuer(X500Name x500Name) {
        this.principalName = x500Name.toString();
    }

    public X500Name toX500Name() {
        return new X500Name(principalName);
    }
}