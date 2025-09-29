import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CaCertificate } from '../models/ca-certificate.model';
import { CertificateService } from '../certificate.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-csr-self-upload',
  standalone: false,
  templateUrl: './csr-self-upload.component.html',
  styleUrl: './csr-self-upload.component.css'
})
export class CsrSelfUploadComponent implements OnInit {
  csrSelfForm: FormGroup;
  selectedFile: File | null = null;
  caList: CaCertificate[] = [];

  constructor(
    private fb: FormBuilder, 
    private service: CertificateService,
    private toaster: ToastrService
  ) {
    this.csrSelfForm = this.fb.group({
      caCertificateId: ['', Validators.required],
      validTo: ['', Validators.required]
    });
    
  }

  ngOnInit(): void {
    this.loadCaCertificates();
  }

  loadCaCertificates(): void {
    this.service.getAvailableCaCertificates().subscribe({
      next: (data) => this.caList = data,
      error: (err) => console.error('Error loading CA certificates', err)
    });
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
      },
      error: (err) => {
        console.error('Error creating CSR self-generated request', err);
        this.toaster.error('Failed to create CSR self-generated request.', 'Error');
      }
    });
  }
}
