export interface CreateRootCertificateRequest {
  validFrom: string;
  validTo: string;
  keyUsages: string[];
  extendedKeyUsages: string[];
  commonName: string;
  organization: string;
  country: string;
  organizationalUnit: string;
  state: string;
  locality: string;
}
