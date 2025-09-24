import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TemplateService} from '../template.service';
import {switchMap} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {CertificateTemplate} from '../model/certificate-template.model';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-edit-template',
  standalone: false,
  templateUrl: './edit-template.component.html',
  styleUrl: './edit-template.component.css'
})
export class EditTemplateComponent implements OnInit {
  id?: number;
  editTemplateForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    issuer: new FormControl('', Validators.required),
    commonNameRegex: new FormControl('', Validators.required),
    sanRegex: new FormControl('', Validators.required),
    ttlDays: new FormControl(0, [Validators.required, Validators.min(1)]),
    keyUsage: new FormControl('', Validators.required),
    extendedKeyUsage: new FormControl('', Validators.required),
  });

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
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
          this.editTemplateForm.patchValue(template);
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
      error: () => this.toasterService.success("Failed to update template.")
    });
  }
}
