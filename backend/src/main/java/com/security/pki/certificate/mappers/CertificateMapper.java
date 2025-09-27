package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.CertificateDetailsResponseDto;
import com.security.pki.certificate.dtos.CertificateResponseDto;
import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.dtos.PartyDto;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.CertificateType;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.shared.models.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CertificateMapper {
    private final ModelMapper modelMapper;

    // NOTE: refactor method below when you add more attr to CreateCertificateDto
    public Certificate fromRequest(CreateCertificateDto request) {
        return modelMapper.map(request, Certificate.class);
    }

    public CertificateResponseDto toResponse(Certificate certificate) {
        CertificateResponseDto response = modelMapper.map(certificate, CertificateResponseDto.class);
        if (certificate.getParent() != null)
            response.setIssuerEmail(certificate.getParent().getOwner().getEmail());
        else
            response.setIssuerEmail(certificate.getOwner().getEmail());
        response.setSubjectEmail(certificate.getOwner().getEmail());
        response.setCertificateType(getType(certificate));
        return response;
    }

    public CertificateDetailsResponseDto toDetailsResponse(Certificate certificate) {
        if (certificate == null) return null;
        CertificateDetailsResponseDto dto = modelMapper.map(certificate, CertificateDetailsResponseDto.class);

        dto.setCertificateType(getType(certificate));
        dto.setSubject(toPartyDto(certificate.getSubject()));
        dto.setIssuer(toPartyDto(certificate.getIssuer()));
        dto.setStatus(certificate.getStatus().name());
        dto.setCanSign(certificate.isCanSign());
        dto.setOwnerEmail(certificate.getOwner() != null ? certificate.getOwner().getEmail() : null);

        try {
            X509Certificate x509 = toX509(certificate);
            dto.setKeyUsages(extractKeyUsages(x509));
            dto.setExtendedKeyUsages(CertificateUtils.mapEkuOids(extractExtendedKeyUsages(x509)));
        } catch (Exception ignored) {
            dto.setKeyUsages(Collections.emptyList());
            dto.setExtendedKeyUsages(Collections.emptyList());
        }

        return dto;
    }


    private PartyDto toPartyDto(Object party) {
        if (party == null) return null;

        String principalName;
        if (party instanceof Subject s) {
            principalName = s.getPrincipalName();
        } else if (party instanceof Issuer i) {
            principalName = i.getPrincipalName();
        } else {
            return null;
        }

        X500Name x500 = new X500Name(principalName);
        return PartyDto.builder()
                .principalName(principalName)
                .email(getRdnValue(x500, BCStyle.E))
                .country(getRdnValue(x500, BCStyle.C))
                .organizationUnit(getRdnValue(x500, BCStyle.OU))
                .organizationName(getRdnValue(x500, BCStyle.O))
                .commonName(getRdnValue(x500, BCStyle.CN))
                .build();
    }

    public PagedResponse<CertificateResponseDto> toPagedResponse(Page<Certificate> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    private String getRdnValue(X500Name name, ASN1ObjectIdentifier oid) {
        RDN[] rdns = name.getRDNs(oid);
        if (rdns != null && rdns.length > 0) {
            AttributeTypeAndValue atv = rdns[0].getFirst();
            if (atv != null) {
                return atv.getValue().toString();
            }
        }
        return null;
    }

    public X509Certificate toX509(Certificate cert) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(cert.getCertificateData()));
    }

    private List<String> extractKeyUsages(X509Certificate x509) {
        List<String> usages = new ArrayList<>();
        boolean[] ku = x509.getKeyUsage();
        if (ku == null) return usages;

        if (ku[0]) usages.add("Digital Signature");
        if (ku[1]) usages.add("Non Repudiation");
        if (ku[2]) usages.add("Key Encipherment");
        if (ku[3]) usages.add("Data Encipherment");
        if (ku[4]) usages.add("Key Agreement");
        if (ku[5]) usages.add("Key Cert Sign");
        if (ku[6]) usages.add("CRL Sign");
        if (ku[7]) usages.add("Encipher Only");
        if (ku[8]) usages.add("Decipher Only");
        return usages;
    }

    private List<String> extractExtendedKeyUsages(X509Certificate x509) throws Exception {
        List<String> list = x509.getExtendedKeyUsage();
        return list != null ? list : List.of();
    }

    private CertificateType getType(Certificate certificate) {
        if (certificate.getParent() == null) {
            return CertificateType.ROOT;
        } else if (certificate.isCanSign()) {
            return CertificateType.INTERMEDIATE;
        } else {
            return CertificateType.END_ENTITY;
        }
    }
}