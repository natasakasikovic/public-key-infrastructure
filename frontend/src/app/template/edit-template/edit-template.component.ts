import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TemplateService} from '../template.service';
import {forkJoin, switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {CertificateTemplate} from '../model/certificate-template.model';
import {ToastrService} from 'ngx-toastr';
import {EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS} from '../../shared/constants/certificate-options';
import {regexValidator} from '../validators/regex-validator.validator';
import {CertificateResponse} from '../../certificate/models/certificate-response.model';
import {MatTableDataSource} from '@angular/material/table';
import {PagedResponse} from '../../shared/model/paged-response';
import {CertificateService} from '../../certificate/certificate.service';
import {PageEvent} from '@angular/material/paginator';

@Component({
  selector: 'app-edit-template',
  standalone: false,
  templateUrl: './edit-template.component.html',
  styleUrl: './edit-template.component.css'
})
export class EditTemplateComponent implements OnInit {
  id?: string;
  extendedKeyUsageOptions: string[] = EXTENDED_KEY_USAGE_OPTIONS;
  keyUsageOptions: string[] = KEY_USAGE_OPTIONS;
  certificates: CertificateResponse[] = [];
  dataSource = new MatTableDataSource<CertificateResponse>(this.certificates);
  totalElements = 0;
  pageSize = 10;
  selectedCertificate: CertificateResponse | null = null;
  displayedCertificateColumns: string[] = ['serialNumber', 'certificateType', 'issuerEmail', 'subjectEmail'];

  editTemplateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    signingCertificateId: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('', [Validators.required, regexValidator()]),
    sanRegex: new FormControl('', [Validators.required, regexValidator()]),
    ttlDays: new FormControl(0, [Validators.required, Validators.min(1)]),
    keyUsages: new FormControl([]),
    extendedKeyUsages: new FormControl([]),
  });

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
    private certificateService: CertificateService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  get keyUsages(): string[] {
    return this.editTemplateForm.get('keyUsages')?.value || [];
  }

  get extendedKeyUsages(): string[] {
    return this.editTemplateForm.get('extendedKeyUsages')?.value || [];
  }

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap(paramMap => {
          const id = paramMap.get('id');
          if (!id) throw new Error('Template ID is required in the route.');
          this.id = id;
          return forkJoin({
            certificates: this.certificateService.getValidCACertificates(0, this.pageSize),
            template: this.templateService.getTemplate(id)
          });
        })
      )
      .subscribe({
        next: ({ certificates, template }) => {
          this.dataSource.data = certificates.content;
          this.totalElements = certificates.totalElements;

          this.editTemplateForm.patchValue({ ...template });
          this.selectedCertificate =
            certificates.content.find(c => c.id === template.signingCertificateId) ?? null;
        }
      });
  }


  onUpdate(): void {
    if(this.editTemplateForm.invalid) return;
    if(!this.id) return;
    const request = this.editTemplateForm.value;
    this.templateService.updateTemplate(this.id, request).subscribe({
      next: () => {
        this.toasterService.success("Template updated successfully.");
        void this.router.navigate(['/templates']);
      },
      error: () => this.toasterService.error("Failed to update template.")
    });
  }

  onPageChange(event: PageEvent): void {
    this.fetchCertificates(event.pageIndex, event.pageSize);
  }

  onCheckboxChange(event: Event, controlName: 'keyUsages' | 'extendedKeyUsages'): void {
    const checkbox = event.target as HTMLInputElement;
    const currentArray = this.editTemplateForm.get(controlName)?.value as string[];

    if (checkbox.checked) {
      this.editTemplateForm.get(controlName)?.setValue([...currentArray, checkbox.value]);
    } else {
      this.editTemplateForm.get(controlName)?.setValue(
        currentArray.filter(item => item !== checkbox.value)
      );
    }
    this.editTemplateForm.get(controlName)?.markAsTouched();
  }

  onCertificateSelected(certificate: CertificateResponse) {
    this.editTemplateForm.controls['signingCertificateId'].setValue(
      certificate.id
    );
    this.selectedCertificate = certificate;
    this.editTemplateForm.controls['signingCertificateId']
      .setValue(certificate.id);
  }

  viewDetails(certificate: CertificateResponse): void {
    void this.router.navigate(['certificate', certificate.id]);
  }

  private fetchCertificates(pageIndex: number, pageSize: number): void {
    this.certificateService.getValidCACertificates(pageIndex, pageSize).subscribe({
      next: (response: PagedResponse<CertificateResponse>) => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
      },
    });
  }

}
