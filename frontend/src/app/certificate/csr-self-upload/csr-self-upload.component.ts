import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CertificateService } from '../certificate.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { CertificateResponse } from '../models/certificate-response.model';
import { PagedResponse } from '../../shared/model/paged-response';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-csr-self-upload',
  standalone: false,
  templateUrl: './csr-self-upload.component.html',
  styleUrl: './csr-self-upload.component.css'
})
export class CsrSelfUploadComponent implements OnInit {
  csrSelfForm: FormGroup;
  selectedFile: File | null = null;
    displayedCertificateColumns: string[] = [
    'serialNumber',
    'certificateType',
    'issuerMail',
    'subjectMail',
    'details',
  ];
  selectedCertificate: CertificateResponse | null = null;
  certificateDataSource = new MatTableDataSource<CertificateResponse>([]);

  totalElements = 0;
  pageSize = 5;

  constructor(
    private fb: FormBuilder, 
    private service: CertificateService,
    private toaster: ToastrService,
    private router: Router
  ) {
    this.csrSelfForm = this.fb.group({
      caCertificateId: ['', Validators.required],
      validTo: ['', Validators.required]
    });
    
  }

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

  viewDetails(certificate: CertificateResponse): void {
    void this.router.navigate(['certificate', certificate.id]);
  }

  onCertificateSelected(certificate: CertificateResponse) {
    this.selectedCertificate = certificate;
    this.csrSelfForm.controls['caCertificateId'].setValue(
      certificate.id
    );
  }

  onCertificatePageChange(event: PageEvent): void {
    this.fetchCertificates(event.pageIndex, event.pageSize);
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    if (this.csrSelfForm.invalid) {
      this.toaster.error('Please fill in all required fields.', 'Form Incomplete');
      return;
    }

    if (!this.selectedFile) {
      this.toaster.error('Please select a PEM file to upload.', 'File Missing');
      return;
    }

    const formData = new FormData();
    formData.append('caCertificateId', this.csrSelfForm.get('caCertificateId')?.value);
    formData.append('validTo', this.csrSelfForm.get('validTo')?.value);
    formData.append('pemFile', this.selectedFile);
    this.service.createCSRSelfGenerate(
      this.csrSelfForm.get('caCertificateId')?.value,
      this.csrSelfForm.get('validTo')?.value,
      this.selectedFile
    ).subscribe({
      next: () => {
        this.toaster.success('CSR self-generated request created successfully.', 'Success');
        this.csrSelfForm.reset();
        this.selectedFile = null;
        void this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Error creating CSR self-generated request', err);
        this.toaster.error('Failed to create CSR self-generated request.', 'Error');
      }
    });
  }
}
