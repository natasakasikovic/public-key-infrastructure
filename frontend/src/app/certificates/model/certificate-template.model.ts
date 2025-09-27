import {Issuer} from './issuer.model';

export interface CertificateTemplate {
  id?: number;
  name: string;
  issuer: Issuer;
  commonNameRegex: string;
  sanRegex: string;
  ttlDays: number;
  keyUsages: string[];
  extendedKeyUsages: string[];
}
