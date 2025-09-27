import {Component, OnInit} from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CertificateService } from '../certificate.service';
import {EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS} from '../../shared/constants/certificate-options';

@Component({
  standalone: false,
  selector: 'app-root-certificate-issuance',
  templateUrl: './root-certificate-issuance.component.html',
  styleUrls: ['./root-certificate-issuance.component.css'],
})
export class RootCertificateIssuanceComponent implements OnInit {
  rootCertForm!: FormGroup;
  keyUsageOptions = KEY_USAGE_OPTIONS;
  extendedKeyUsageOptions = EXTENDED_KEY_USAGE_OPTIONS;

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
