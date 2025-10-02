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
import {CertificateTemplate} from '../../template/model/certificate-template.model';
import {HttpErrorResponse} from '@angular/common/http';

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
  template: CertificateTemplate | null = null;
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
    subjectAlternativeNames: new FormArray([]),
  });

  constructor(
    private service: CertificateService,
    private router: Router,
    private toasterService: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchCertificates(0, this.pageSize);
    const state = history.state;
    if (state?.template) {
      this.template = state.template as CertificateTemplate;
      this.applyTemplate(this.template);
    }
  }

  fetchCertificates(pageIndex: number, pageSize: number): void {
    this.service
      .getAuthorizedIssuableCertificates(pageIndex, pageSize)
      .subscribe({
        next: (response: PagedResponse<CertificateResponse>) => {
          this.certificateDataSource.data = response.content;
          this.totalElements = response.totalElements;
          this.selectedCertificate =
            this.certificateDataSource.data.find(c => c.id === this.template?.signingCertificateId) ?? null;
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

  get subjectAlternativeNames(): FormArray {
    return this.certificateForm.get('subjectAlternativeNames') as FormArray;
  }

  addSAN(): void {
    const sanGroup = new FormGroup({
      type: new FormControl('DNS', Validators.required),
      value: new FormControl('', Validators.required),
    });
    if(this.template) {
      sanGroup.setValidators([
        Validators.required,
        Validators.pattern(this.template?.sanRegex)
      ]);
    }
    this.subjectAlternativeNames.push(sanGroup);
  }

  removeSAN(index: number): void {
    this.subjectAlternativeNames.removeAt(index);
  }

  onCertificateSelected(certificate: CertificateResponse) {
    if(this.template) return;
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
      error: (error: HttpErrorResponse) =>
        this.toasterService.error(
          error?.error?.message,
          'Failed to create certificate. Please try again later.'
        ),
    });
  }

  applyTemplate(template: CertificateTemplate): void {
    this.certificateForm.patchValue({
      signingCertificateId: template.signingCertificateId,
      commonName: template.commonNameRegex,
      pathLenConstraint: 0,
    });

    this.certificateForm.get('commonName')?.setValidators([
      Validators.required,
      Validators.pattern(template.commonNameRegex),
    ]);

    this.subjectAlternativeNames.push(
      new FormGroup({
        type: new FormControl('DNS'),
        value: new FormControl(template.sanRegex),
      })
    );

    this.keyUsagesFormArray.clear();
    template.keyUsages.forEach(usage => {
      this.keyUsagesFormArray.push(new FormControl(usage));
    });

    this.extendedKeyUsagesFormArray.clear();
    template.extendedKeyUsages.forEach(usage => {
      this.extendedKeyUsagesFormArray.push(new FormControl(usage));
    });

    this.addValidToCalculation();
  }

  private addValidToCalculation(): void {
    this.certificateForm.get('validFrom')?.valueChanges.subscribe((start: string) => {
      if (start && this.template?.ttlDays) {
        const startDate = new Date(start);
        const endDate = new Date(startDate);
        endDate.setDate(startDate.getDate() + this.template.ttlDays);

        this.certificateForm.get('validTo')?.setValue(endDate.toISOString().split('T')[0]);
      }
    });
  }

}
