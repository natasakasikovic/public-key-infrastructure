export const KEY_USAGE_OPTIONS: string[] = [
  'Digital Signature',
  'Non Repudiation',
  'Key Encipherment',
  'Data Encipherment',
  'Key Agreement',
];

export const EXTENDED_KEY_USAGE_OPTIONS: string[] = [
  'TSL Web Server Authentication',
  'TLS Web Client Authentication',
  'Sign Executable Code',
  'Email Protection',
];

export const REVOCATION_REASONS: string[] = [
  'Unspecified',
  'Key Compromise',
  'CA Compromise',
  'Affiliation Changed',
  'Superseded',
  'Cessation of Operation',
  'Certificate Hold',
  'Remove from CRL',
  'Privilege Withdrawn',
  'AA Compromise'
];
