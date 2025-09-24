import { Component } from '@angular/core';
import {TemplateService} from '../template.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {CertificateTemplate} from '../model/certificate-template.model';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-template-form',
  standalone: false,
  templateUrl: './template-form.component.html',
  styleUrl: './template-form.component.css'
})
export class TemplateFormComponent {

  templateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    issuer: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('.*\\.example\\.com', Validators.required),
    sanRegex: new FormControl('.*', Validators.required),
    ttlDays: new FormControl(365, [Validators.required, Validators.min(1)]),
    keyUsage: new FormControl('digitalSignature,keyEncipherment', Validators.required),
    extendedKeyUsage: new FormControl('serverAuth,clientAuth', Validators.required),
  });

  constructor(private templateService: TemplateService) {}

  onCreate(): void {
    if(this.templateForm.invalid) return;
    const request: CertificateTemplate = this.templateForm.value;
    this.templateService.createTemplate(request).subscribe({
      next: () => {
        console.log("Success");
        this.templateForm.reset({
          ttlDays: 365,
          keyUsage: 'digitalSignature,keyEncipherment',
          extendedKeyUsage: 'serverAuth,clientAuth'
        });
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      }
    });
  }

}
