import { Component, OnInit } from '@angular/core';
import { EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS } from '../../shared/constants/certificate-options';
import { CertificateService } from '../certificate.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { PagedResponse } from '../../shared/model/paged-response';
import { CertificateResponse } from '../models/certificate-response.model';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-csr-auto-generate',
  standalone: false,
  templateUrl: './csr-auto-generate.component.html',
  styleUrl: './csr-auto-generate.component.css'
})
export class CsrAutoGenerateComponent implements OnInit {

  selectedCertificate: CertificateResponse | null = null;
  keyUsageOptions = KEY_USAGE_OPTIONS;
  extendedKeyUsageOptions = EXTENDED_KEY_USAGE_OPTIONS;

  // certificates table
  displayedCertificateColumns: string[] = [
    'serialNumber',
    'certificateType',
    'issuerMail',
    'subjectMail',
    'details',
  ];
  certificateDataSource = new MatTableDataSource<CertificateResponse>([]);

  certificateForm: FormGroup = new FormGroup({
    commonName: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
    organizationalUnit: new FormControl(''),
    state: new FormControl(''),
    locality: new FormControl(''),
    validFrom: new FormControl('', Validators.required),
    validTo: new FormControl('', Validators.required),
    signingCertificateId: new FormControl('', Validators.required),
    pathLenConstraint: new FormControl(''),
    keyUsages: new FormArray([]),
    extendedKeyUsages: new FormArray([]),
  });

  totalElements = 0;
  pageSize = 5;
  
  constructor(
    private service: CertificateService,
    private router: Router,
    private toasterService: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchCertificates(0, this.pageSize);
  }

  fetchCertificates(pageIndex: number, pageSize: number): void {
    this.service.getValidCACertificates(pageIndex, pageSize).subscribe({
      next: (response: PagedResponse<CertificateResponse>) => {
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
    this.selectedCertificate = certificate;
    this.certificateForm.controls['signingCertificateId'].setValue(
      certificate.id
    );
  }

  createCertificate() {
    if (this.certificateForm.invalid) return;

    const payload = {
      ...this.certificateForm.value,
      canSign:
        this.certificateForm.get('certificateType')?.value === 'INTERMEDIATE',
      pathLenConstraint: 0
    };

    this.service.createSubordinateCertificate(payload).subscribe({
      next: () => {
        this.toasterService.success(
          'Certificate has been created successfully!'
        );
        void this.router.navigate(['/home']);
      },
      error: (err) => {
        console.log(err)
        this.toasterService.error(
          'Failed to create certificate. Please try again later.'
        );
      }
    });
  }

}
