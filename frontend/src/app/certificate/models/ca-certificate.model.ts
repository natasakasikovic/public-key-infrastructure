export interface CaCertificate {
  id: string;
  commonName: string;
  organization?: string;
  serialNumber: string;
  validFrom: string;
  validTo: string;
}