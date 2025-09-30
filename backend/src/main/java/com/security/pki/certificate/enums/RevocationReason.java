package com.security.pki.certificate.enums;

public enum RevocationReason {
    UNSPECIFIED,             // 0 - Reason not specified
    KEY_COMPROMISE,          // 1 - The private key has been compromised
    CA_COMPROMISE,           // 2 - The issuing CA's private key has been compromised
    AFFILIATION_CHANGED,     // 3 - The subject’s affiliation has changed (e.g., organization or role)
    SUPERSEDED,              // 4 - The certificate has been replaced by a new one
    CESSATION_OF_OPERATION,  // 5 - The entity no longer uses the certificate (service shut down, org closed, etc.)
    CERTIFICATE_HOLD,        // 6 - The certificate is temporarily suspended (can be reinstated)
    REMOVE_FROM_CRL,         // 8 - The certificate was previously on hold and is now removed from the CRL (made valid again)
    PRIVILEGE_WITHDRAWN,     // 9 - The privileges granted to the subject are withdrawn
    AA_COMPROMISE            // 10 - The Attribute Authority’s private key has been compromised
}
