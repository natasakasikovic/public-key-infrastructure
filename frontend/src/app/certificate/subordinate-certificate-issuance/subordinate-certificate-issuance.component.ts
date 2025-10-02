import { Component, OnInit } from '@angular/core';
import {
  EXTENDED_KEY_USAGE_OPTIONS,
  KEY_USAGE_OPTIONS,
} from '../../shared/constants/certificate-options';
import {
  FormArray,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { UserResponse } from '../../user/model/user-response.model';
import { CertificateService } from '../certificate.service';
import { MatTableDataSource } from '@angular/material/table';
import { PagedResponse } from '../../shared/model/paged-response';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { CertificateResponse } from '../models/certificate-response.model';
import { UserService } from '../../user/user.service';
import { ToastrService } from 'ngx-toastr';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-subordinate-certificate-issuance',
  standalone: false,
  templateUrl: './subordinate-certificate-issuance.component.html',
  styleUrl: './subordinate-certificate-issuance.component.css',
})
export class SubordinateCertificateIssuanceComponent implements OnInit {
  keyUsageOptions = KEY_USAGE_OPTIONS;
  extendedKeyUsageOptions = EXTENDED_KEY_USAGE_OPTIONS;

  // certificates table
  displayedCertificateColumns: string[] = [
    'serialNumber',
    'certificateType',
    'issuerMail',
    'subjectMail',
  ];
  certificateDataSource = new MatTableDataSource<CertificateResponse>([]);

  // users table
  displayedUserColumns: string[] = [
    'firstName',
    'lastName',
    'email',
    'organization',
  ];
  userDataSource = new MatTableDataSource<UserResponse>([]);
  selectedCertificate: CertificateResponse | null = null;
  selectedUser: UserResponse | null = null;

  totalElements = 0;
  pageSize = 5;

  certificateForm: FormGroup = new FormGroup({
    commonName: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
    organizationalUnit: new FormControl(''),
    state: new FormControl(''),
    locality: new FormControl(''),
    userId: new FormControl('', Validators.required),
    validFrom: new FormControl('', Validators.required),
    validTo: new FormControl('', Validators.required),
    signingCertificateId: new FormControl('', Validators.required),
    certificateType: new FormControl('', Validators.required),
    pathLenConstraint: new FormControl(''),
    keyUsages: new FormArray([]),
    extendedKeyUsages: new FormArray([]),
    subjectAlternativeNames: new FormArray([]),
  });

  constructor(
    private service: CertificateService,
    private userService: UserService,
    private router: Router,
    private toasterService: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchUsers(0, this.pageSize);
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

  fetchUsers(pageIndex: number, pageSize: number): void {
    this.userService.getAll(pageIndex, pageSize).subscribe({
      next: (response: PagedResponse<UserResponse>) => {
        this.userDataSource.data = response.content;
        this.totalElements = response.totalElements;
      },
    });
  }

  onCertificatePageChange(event: PageEvent): void {
    this.fetchCertificates(event.pageIndex, event.pageSize);
  }

  onUserPageChanged(event: PageEvent): void {
    this.fetchUsers(event.pageIndex, event.pageSize);
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

  onUserSelected(user: UserResponse) {
    this.certificateForm.controls['userId'].setValue(user.id);
    this.selectedUser = user;
    this.certificateForm.controls['userId']
      .setValue(user.id);
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
      error: (error: HttpErrorResponse) =>
        this.toasterService.error(
          error?.error?.message,
          'Failed to create certificate.'
        ),
    });
  }

  addSAN(): void {
    const sanGroup = new FormGroup({
      type: new FormControl('DNS', Validators.required),
      value: new FormControl('', Validators.required),
    });
    this.subjectAlternativeNames.push(sanGroup);
  }

  removeSAN(index: number): void {
    this.subjectAlternativeNames.removeAt(index);
  }
}
