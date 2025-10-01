import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { CertificateService } from '../certificate.service';
import {
  EXTENDED_KEY_USAGE_OPTIONS,
  KEY_USAGE_OPTIONS,
} from '../../shared/constants/certificate-options';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  standalone: false,
  selector: 'app-root-certificate-issuance',
  templateUrl: './root-certificate-issuance.component.html',
  styleUrls: ['./root-certificate-issuance.component.css'],
})
export class RootCertificateIssuanceComponent implements OnInit {
  rootCertForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private service: CertificateService,
    private toasterService: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.rootCertForm = this.fb.group({
      commonName: ['', Validators.required],
      organization: ['', Validators.required],
      country: [''],
      organizationalUnit: [''],
      state: [''],
      locality: [''],
      validFrom: ['', Validators.required],
      validTo: ['', Validators.required],
    });
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

  onSubmit() {
    if (this.rootCertForm.invalid) return;

    this.service.createRootCertificate(this.rootCertForm.value).subscribe({
      next: () => {
        this.toasterService.success(
          'Certificate has been created successfully!'
        );
        void this.router.navigate(['/home']);
      },
      error: () =>
        this.toasterService.error(
          'Failed to create certificate. Please try again later.'
        ),
    });
  }
}
