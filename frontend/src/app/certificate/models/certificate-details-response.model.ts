export interface CertificateDetails {
  subject: PartyInfo;
  issuer: PartyInfo;
  certificateType: string;
  serialNumber: string;
  validFrom: Date;
  validTo: Date;
  keyUsages: string[];
  extendedKeyUsages: string[];
  status: string
}

export interface PartyInfo {
  country: string;
  organizationUnit: string;
  organizationName: string;
  commonName: string;
}
