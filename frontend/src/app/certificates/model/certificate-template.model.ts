export interface CertificateTemplate {
  id?: number;
  name: string;
  issuer: string;
  commonNameRegex: string;
  sanRegex: string;
  ttlDays: number;
  keyUsage: string;
  extendedKeyUsage: string;
}
