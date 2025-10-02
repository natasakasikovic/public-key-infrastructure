import {Component, OnInit} from '@angular/core';
import {
  EXTENDED_KEY_USAGE_OPTIONS,
  KEY_USAGE_OPTIONS,
} from '../../shared/constants/certificate-options';
import { MatTableDataSource } from '@angular/material/table';
import { CertificateResponse } from '../models/certificate-response.model';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { CertificateService } from '../certificate.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { PagedResponse } from '../../shared/model/paged-response';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-ca-certificate-issuance',
  standalone: false,
  templateUrl: './ca-certificate-issuance.component.html',
  styleUrl: './ca-certificate-issuance.component.css',
})
export class CaCertificateIssuanceComponent implements OnInit {
  keyUsageOptions = KEY_USAGE_OPTIONS;
  extendedKeyUsageOptions = EXTENDED_KEY_USAGE_OPTIONS;
  selectedCertificate: CertificateResponse | null = null;

  // certificates table
  displayedCertificateColumns: string[] = [
    'serialNumber',
    'certificateType',
    'issuerMail',
    'subjectMail',
    'details',
  ];
  certificateDataSource = new MatTableDataSource<CertificateResponse>([]);

  totalElements = 0;
  pageSize = 5;

  certificateForm: FormGroup = new FormGroup({
    commonName: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
    organizationalUnit: new FormControl(''),
    state: new FormControl(''),
    locality: new FormControl(''),
    validFrom: new FormControl('', Validators.required),
    validTo: new FormControl('', Validators.required),
    signingCertificateId: new FormControl('', Validators.required),
    pathLenConstraint: new FormControl('', Validators.required),
    certificateType: new FormControl('', Validators.required),
    keyUsages: new FormArray([]),
    extendedKeyUsages: new FormArray([]),
  });

  constructor(
    private service: CertificateService,
    private router: Router,
    private toasterService: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchCertificates(0, this.pageSize);
  }

  fetchCertificates(pageIndex: number, pageSize: number): void {
    this.service
      .getAuthorizedIssuableCertificates(pageIndex, pageSize)
      .subscribe({
        next: (response: PagedResponse<CertificateResponse>) => {
          console.log(response);
          this.certificateDataSource.data = response.content;
          this.totalElements = response.totalElements;
        },
      });
  }

  onCertificatePageChange(event: PageEvent): void {
    this.fetchCertificates(event.pageIndex, event.pageSize);
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

  viewDetails(certificate: CertificateResponse): void {
    void this.router.navigate(['certificate', certificate.id]);
  }

  get keyUsagesFormArray(): FormArray {
    return this.certificateForm.get('keyUsages') as FormArray;
  }

  get extendedKeyUsagesFormArray(): FormArray {
    return this.certificateForm.get('extendedKeyUsages') as FormArray;
  }

  onCertificateSelected(certificate: CertificateResponse) {
    this.certificateForm.controls['signingCertificateId'].setValue(
      certificate.id
    );
    this.selectedCertificate = certificate;
    this.certificateForm.controls['signingCertificateId']
      .setValue(certificate.id);
  }

  createCertificate() {
    if (this.certificateForm.invalid) return;

    const payload = {
      ...this.certificateForm.value,
      canSign:
        this.certificateForm.get('certificateType')?.value === 'INTERMEDIATE',
    };

    this.service.createSubordinateCertificate(payload).subscribe({
      next: () => {
        this.toasterService.success(
          'Certificate has been created successfully!'
        );
        void this.router.navigate(['/home']);
      },
      error: () =>
        this.toasterService.error(
          'Failed to create certificate. Please try again later.'
        ),
    });
  }
}
