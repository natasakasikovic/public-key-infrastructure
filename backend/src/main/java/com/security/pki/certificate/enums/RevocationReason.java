package com.security.pki.certificate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RevocationReason {
    UNSPECIFIED(0, "Unspecified"),
    KEY_COMPROMISE(1, "Key Compromise"),
    CA_COMPROMISE(2, "CA Compromise"),
    AFFILIATION_CHANGED(3, "Affiliation Changed"),
    SUPERSEDED(4, "Superseded"),
    CESSATION_OF_OPERATION(5, "Cessation of Operation"),
    CERTIFICATE_HOLD(6, "Certificate Hold"),
    REMOVE_FROM_CRL(8, "Remove from CRL"),
    PRIVILEGE_WITHDRAWN(9, "Privilege Withdrawn"),
    AA_COMPROMISE(10, "AA Compromise");

    private final int code;
    private final String label;

    RevocationReason(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static RevocationReason fromLabel(String label) {
        for (RevocationReason r : values()) {
            if (r.label.equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown reason: " + label);
    }
}
