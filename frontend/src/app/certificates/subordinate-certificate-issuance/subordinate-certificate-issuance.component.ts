import { Component, OnInit } from '@angular/core';
import {
  EXTENDED_KEY_USAGE_OPTIONS,
  KEY_USAGE_OPTIONS,
  SUBORDINATE_CERTIFICATE_TYPE,
} from '../../shared/constants/certificate-options';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { UserResponse } from '../../auth/model/user-response.model';
import { CertificateResponse } from '../model/certificate-response.model';
import { CertificateService } from '../../certificate/certificate.service';

@Component({
  selector: 'app-subordinate-certificate-issuance',
  standalone: false,
  templateUrl: './subordinate-certificate-issuance.component.html',
  styleUrl: './subordinate-certificate-issuance.component.css',
})
export class SubordinateCertificateIssuanceComponent implements OnInit {
  keyUsageOptions = KEY_USAGE_OPTIONS;
  extendedKeyUsageOptions = EXTENDED_KEY_USAGE_OPTIONS;

  users: UserResponse[] = [];
  certificates: CertificateResponse[] = [];

  certificateForm: FormGroup = new FormGroup({
    user: new FormControl('', Validators.required),
    dateFrom: new FormControl('', Validators.required),
    dateTo: new FormControl('', Validators.required),
    certificateType: new FormControl('', Validators.required),
    keyUsages: new FormControl([]),
    extendedKeyUsages: new FormControl([]),
  });

  constructor(private fb: FormBuilder, service: CertificateService) {}

  ngOnInit(): void {
    // TODO: load users and certificates
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

  get keyUsagesFormArray(): FormArray {
    return this.certificateForm.get('keyUsages') as FormArray;
  }

  get extendedKeyUsagesFormArray(): FormArray {
    return this.certificateForm.get('extendedKeyUsages') as FormArray;
  }

  // TODO: implement methods below
  toggleKeyUsage(option: string) {}
  toggleExtendedKeyUsage(option: string) {}
  onDateChange() {}
  selectCertificate(cert: any) {}
  createCertificate() {}
}
