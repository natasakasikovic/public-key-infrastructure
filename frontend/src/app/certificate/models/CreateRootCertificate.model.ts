export interface CreateRootCertificateRequest {
  startDate: string;
  endDate: string;
  keyUsages: string[];
  extendedKeyUsages: string[];
}
