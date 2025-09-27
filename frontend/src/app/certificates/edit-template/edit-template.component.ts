import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TemplateService} from '../template.service';
import {switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {CertificateTemplate} from '../model/certificate-template.model';
import {ToastrService} from 'ngx-toastr';
import {EXTENDED_KEY_USAGE_OPTIONS, KEY_USAGE_OPTIONS} from '../../shared/constants/certificate-options';
import {HttpErrorResponse} from '@angular/common/http';
import {regexValidator} from '../validators/regex-validator.validator';

@Component({
  selector: 'app-edit-template',
  standalone: false,
  templateUrl: './edit-template.component.html',
  styleUrl: './edit-template.component.css'
})
export class EditTemplateComponent implements OnInit {
  id?: number;
  extendedKeyUsageOptions: string[] = EXTENDED_KEY_USAGE_OPTIONS;
  keyUsageOptions: string[] = KEY_USAGE_OPTIONS;
  editTemplateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    issuer: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('', [Validators.required, regexValidator()]),
    sanRegex: new FormControl('', [Validators.required, regexValidator()]),
    ttlDays: new FormControl(0, [Validators.required, Validators.min(1)]),
    keyUsages: new FormControl([]),
    extendedKeyUsages: new FormControl([]),
  });

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
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
          const id = Number(paramMap.get('id'));
          this.id = id;
          return this.templateService.getTemplate(id);
        })
      )
      .subscribe({
        next: (template: CertificateTemplate) => {
          this.editTemplateForm.patchValue({...template, issuer: template.issuer.principalName});
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
}
