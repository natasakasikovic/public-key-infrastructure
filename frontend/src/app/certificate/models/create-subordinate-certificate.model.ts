import {SAN} from '../../shared/model/san.model';

export interface CreateSubordinateCertificateRequest {
  userId: number; // you will get organization from user on backend
  commonName: string;
  country: string;
  organizationalUnit: string;
  state: string;
  locality: string;
  validFrom: string;
  validTo: string;
  signingCertificateId: string;
  keyUsages: string[];
  extendedKeyUsages: string[];
  canSign: boolean;
  subjectAlternativeNames: SAN[];
  commonNameRegex?: string;
  sanRegex?: string;
  ttlDays?: number;
}
