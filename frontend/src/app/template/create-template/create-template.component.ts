import {Component, OnInit, ViewChild} from '@angular/core';
import { TemplateService } from '../template.service';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import { CertificateTemplate } from '../model/certificate-template.model';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import {EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS} from '../../shared/constants/certificate-options';
import {regexValidator} from '../validators/regex-validator.validator';
import {CertificateResponse} from '../../certificate/models/certificate-response.model';
import {CertificateService} from '../../certificate/certificate.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {PagedResponse} from '../../shared/model/paged-response';

@Component({
  selector: 'app-template-form',
  templateUrl: './create-template.component.html',
  standalone: false,
  styleUrls: ['./create-template.component.css']
})
export class CreateTemplateComponent implements OnInit {
  extendedKeyUsageOptions: string[] = EXTENDED_KEY_USAGE_OPTIONS;
  keyUsageOptions: string[] = KEY_USAGE_OPTIONS;
  selectedIssuer: CertificateResponse | null = null;
  certificates: CertificateResponse[] = [];
  displayedCertificateColumns: string[] = ['serialNumber', 'certificateType', 'issuerEmail', 'subjectEmail'];
  dataSource = new MatTableDataSource<CertificateResponse>(this.certificates);
  totalElements = 0;
  pageSize = 10;
  selectedCertificate: CertificateResponse | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  templateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    signingCertificateId: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('.*\\.example\\.com', [Validators.required, regexValidator()]),
    sanRegex: new FormControl('.*', [Validators.required, regexValidator()]),
    ttlDays: new FormControl(365, [Validators.required, Validators.min(1)]),
    keyUsages: new FormControl([]),
    extendedKeyUsages: new FormControl([]),
  });

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
    private certificateService: CertificateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchCertificates(0, this.pageSize);
  }

  onCreate(): void {
    if (this.templateForm.invalid) return;

    const request: CertificateTemplate = this.templateForm.value;

    this.templateService.createTemplate(request).subscribe({
      next: () => {
        this.toasterService.success("Template created successfully.");
        this.templateForm.reset({
          name: '',
          commonNameRegex: '.*\\.example\\.com',
          sanRegex: '.*',
          ttlDays: 365,
          keyUsages: [],
          extendedKeyUsages: []
        });
        void this.router.navigate(['/templates']);
      },
      error: (error: HttpErrorResponse) => {
        this.toasterService.error(error?.error?.message, "Failed to create template.");
      }
    });
  }

  onCheckboxChange(event: Event, controlName: 'keyUsages' | 'extendedKeyUsages'): void {
    const checkbox = event.target as HTMLInputElement;
    const currentArray = this.templateForm.get(controlName)?.value as string[];

    if (checkbox.checked) {
      this.templateForm.get(controlName)?.setValue([...currentArray, checkbox.value]);
    } else {
      this.templateForm.get(controlName)?.setValue(
        currentArray.filter(item => item !== checkbox.value)
      );
    }
    this.templateForm.get(controlName)?.markAsTouched();
  }

  onPageChange(event: PageEvent): void {
    this.fetchCertificates(event.pageIndex, event.pageSize);
  }

  private fetchCertificates(pageIndex: number, pageSize: number): void {
    this.certificateService.getAuthorizedIssuableCertificates(pageIndex, pageSize).subscribe({
      next: (response: PagedResponse<CertificateResponse>) => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
      },
    });
  }

  onCertificateSelected(certificate: CertificateResponse) {
    this.templateForm.controls['signingCertificateId'].setValue(
      certificate.id
    );
    this.selectedCertificate = certificate;
    this.templateForm.controls['signingCertificateId']
      .setValue(certificate.id);
  }

  viewDetails(certificate: CertificateResponse): void {
    void this.router.navigate(['certificate', certificate.id]);
  }
}
