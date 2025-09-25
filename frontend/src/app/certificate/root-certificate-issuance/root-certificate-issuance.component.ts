import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CertificateService } from '../certificate.service';

@Component({
  standalone: false,
  selector: 'app-root-certificate-issuance',
  templateUrl: './root-certificate-issuance.component.html',
  styleUrls: ['./root-certificate-issuance.component.css'],
})
export class RootCertificateIssuanceComponent {
  rootCertForm!: FormGroup;

  constructor(private fb: FormBuilder, private service: CertificateService) {}

  ngOnInit(): void {
    this.rootCertForm = this.fb.group({
      commonName: [''],
      organization: [''],
      country: [''],
      organizationalUnit: [''],
      state: [''],
      locality: [''],
      validFrom: [''],
      validTo: [''],
      keyUsages: this.fb.array([]),
      extendedKeyUsages: this.fb.array([]),
    });
  }

  // TODO: extract?
  keyUsageOptions: string[] = [
    'Digital Signature',
    'Non Repudiation',
    'Key Encipherment',
    'Data Encipherment',
    'Key Agreement',
  ];

  extendedKeyUsageOptions: string[] = [
    'TSL Web Server Authentication',
    'TLS Web Client Authentication',
    'Sign Executable Code',
    'Email Protection',
  ];

  get keyUsages(): FormArray {
    return this.rootCertForm.get('keyUsages') as FormArray;
  }

  get extendedKeyUsages(): FormArray {
    return this.rootCertForm.get('extendedKeyUsages') as FormArray;
  }

  onCheckboxChange(event: any, formArray: FormArray) {
    if (event.target.checked)
      formArray.push(new FormControl(event.target.value));
    else {
      const index = formArray.controls.findIndex(
        (x) => x.value === event.target.value
      );
      formArray.removeAt(index);
    }
  }

  onSubmit() {
    this.service.createRootCertificate(this.rootCertForm.value).subscribe({
      next: () => {
        console.log('Creation successful'); // TODO: replace with propriate dialogs
      },
      error: (err) => {
        console.error('Creation failed', err);
      },
    });
  }
}
