import { Component } from '@angular/core';
import { TemplateService } from '../template.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CertificateTemplate } from '../model/certificate-template.model';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import {EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS} from '../../shared/constants/certificate-options';
import {regexValidator} from '../validators/regex-validator.validator';

@Component({
  selector: 'app-template-form',
  templateUrl: './create-template.component.html',
  standalone: false,
  styleUrls: ['./create-template.component.css']
})
export class CreateTemplateComponent {
  extendedKeyUsageOptions: string[] = EXTENDED_KEY_USAGE_OPTIONS;
  keyUsageOptions: string[] = KEY_USAGE_OPTIONS;
  templateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    issuer: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('.*\\.example\\.com', [Validators.required, regexValidator()]),
    sanRegex: new FormControl('.*', [Validators.required, regexValidator()]),
    ttlDays: new FormControl(365, [Validators.required, Validators.min(1)]),
    keyUsages: new FormControl([]),
    extendedKeyUsages: new FormControl([]),
  });

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
    private router: Router
  ) {}

  onCreate(): void {
    if (this.templateForm.invalid) return;

    const request: CertificateTemplate = this.templateForm.value;

    this.templateService.createTemplate(request).subscribe({
      next: () => {
        this.toasterService.success("Template created successfully.");
        this.templateForm.reset({
          name: '',
          issuer: '',
          commonNameRegex: '.*\\.example\\.com',
          sanRegex: '.*',
          ttlDays: 365,
          keyUsages: [],
          extendedKeyUsages: []
        });
        void this.router.navigate(['/templates']);
      },
      error: (error: HttpErrorResponse) => {
        this.toasterService.error("Failed to create template.");
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
}
