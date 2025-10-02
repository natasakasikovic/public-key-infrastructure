package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.certificate.*;
import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.exceptions.CertificateParsingException;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.enums.CertificateType;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.shared.models.PagedResponse;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CertificateMapper {
  private final ModelMapper modelMapper;

  public Certificate fromRequest(CreateSubordinateCertificateDto request) {
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
    if (certificate == null)
      return null;
    CertificateDetailsResponseDto response = modelMapper.map(certificate, CertificateDetailsResponseDto.class);

    response.setCertificateType(getType(certificate));
    response.setSubject(toPartyDto(certificate.getSubject()));
    response.setIssuer(toPartyDto(certificate.getIssuer()));
    response.setStatus(certificate.getStatus().name());
    response.setCanSign(certificate.isCanSign());
    response.setOwnerEmail(certificate.getOwner() != null ? certificate.getOwner().getEmail() : null);

    try {
      X509Certificate x509 = toX509(certificate);
      response.setKeyUsages(extractKeyUsages(x509));
      response.setExtendedKeyUsages(CertificateUtils.mapEkuOids(extractExtendedKeyUsages(x509)));
    } catch (Exception ignored) {
      response.setKeyUsages(Collections.emptyList());
      response.setExtendedKeyUsages(Collections.emptyList());
    }

    return response;
  }

  public Certificate toCertificateEntity(CreateSubordinateCertificateDto request, BigInteger serialNumber, X500Name subjectX500Name, X500Name issuerX500Name, User user, Certificate signingCertificate) {
    return Certificate.builder()
            .id(UUID.randomUUID())
            .serialNumber(serialNumber.toString())
            .subject(new Subject(subjectX500Name))
            .issuer(new Issuer(issuerX500Name))
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .owner(user)
            .subjectAlternativeNames(request.getSubjectAlternativeNames())
            .parent(signingCertificate)
            .status(Status.ACTIVE)
            .canSign(request.getCanSign())
            .pathLenConstraint(request.getPathLenConstraint() != null ? request.getPathLenConstraint() : signingCertificate.getPathLenConstraint() - 1)
            .build();
  }

  public Certificate toCertificateEntity(CreateRootCertificateRequest request, X500Name x500Name, BigInteger serialNumber) {
    return Certificate.builder()
            .id(UUID.randomUUID())
            .serialNumber(serialNumber.toString())
            .subject(new Subject(x500Name))
            .issuer(new Issuer(x500Name))
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .status(Status.ACTIVE)
            .canSign(true)
            .pathLenConstraint(null) // for root
            .build();
  }

  public Page<Certificate> toPage(List<Certificate> source, Pageable pageable) {
    int total = source.size();
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), total);
    List<Certificate> content;
    if (start >= end) {
      content = Collections.emptyList();
    } else {
      content = source.subList(start, end);
    }
    return new PageImpl<>(content, pageable, total);
  }

  private PartyDto toPartyDto(Object party) {
    if (party == null)
      return null;

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
        page.getTotalElements());
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

  public X509Certificate toX509(Certificate cert)  {
      try {
          CertificateFactory cf = CertificateFactory.getInstance("X.509");
          return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getCertificateData()));
      } catch (CertificateException e) {
          throw new CertificateParsingException("Failed to parse certificate");
      }
  }

  private List<String> extractKeyUsages(X509Certificate x509) {
    List<String> usages = new ArrayList<>();
    boolean[] ku = x509.getKeyUsage();
    if (ku == null)
      return usages;

    if (ku[0])
      usages.add("Digital Signature");
    if (ku[1])
      usages.add("Non Repudiation");
    if (ku[2])
      usages.add("Key Encipherment");
    if (ku[3])
      usages.add("Data Encipherment");
    if (ku[4])
      usages.add("Key Agreement");
    if (ku[5])
      usages.add("Key Cert Sign");
    if (ku[6])
      usages.add("CRL Sign");
    if (ku[7])
      usages.add("Encipher Only");
    if (ku[8])
      usages.add("Decipher Only");
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


  public CreateSubordinateCertificateDto fromCsr(PKCS10CertificationRequest csr,
      User user,
      UUID caId,
      String until) {
    X500Name x500 = csr.getSubject();

    return CreateSubordinateCertificateDto.builder()
            .commonName(getRdnValue(x500, BCStyle.CN) != null ? getRdnValue(x500, BCStyle.CN) : "unknown")
            .organizationalUnit(getRdnValue(x500, BCStyle.OU))
            .country(getRdnValue(x500, BCStyle.C))
            .signingCertificateId(caId)
            .userId(user.getId())
            .validFrom(new Date())
            .validTo(Date.from(
                    LocalDate.parse(until, DateTimeFormatter.ISO_LOCAL_DATE)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()))
            .signingCertificateId(caId)
            .keyUsages(new ArrayList<>())
            .extendedKeyUsages(new ArrayList<>())
            .subjectAlternativeNames(new ArrayList<>())
            .state(getRdnValue(x500, BCStyle.ST))
            .locality(getRdnValue(x500, BCStyle.L))
            .pathLenConstraint(0)
            .canSign(false)
            .build();
  }

}