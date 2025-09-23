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
@NoArgsConstructor
@Embeddable
public class Subject {
    private String principalName;

    public Subject(X500Name x500Name) {
        this.principalName = x500Name.toString();
    }

    public X500Name toX500Name() {
        return new X500Name(principalName);
    }
}