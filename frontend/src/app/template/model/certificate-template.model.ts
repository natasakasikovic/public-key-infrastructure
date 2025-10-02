export interface CertificateTemplate {
  id?: string;
  name: string;
  signingCertificateId: string;
  issuerEmail: string;
  commonNameRegex: string;
  sanRegex: string;
  ttlDays: number;
  keyUsages: string[];
  extendedKeyUsages: string[];
}
