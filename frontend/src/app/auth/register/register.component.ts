import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterRequest } from '../model/register-request.model';
import { AuthService } from '../auth.service';
import { passwordMatchValidator } from '../validators/password-match.validator';
import zxcvbn from 'zxcvbn';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  standalone: false
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;
  passwordScore: number = 0;
  passwordFeedback: string[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toasterService: ToastrService,
  ) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group(
      {
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
        passwordConfirmation: ['', [Validators.required]],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        organization: ['', Validators.required]
      },
      {
        validators: passwordMatchValidator(),
        updateOn: 'change'
    }
    );

    this.registerForm.get('password')?.valueChanges.subscribe(value => {
      if (value) {
        const result = zxcvbn(value);
        this.passwordScore = result.score;
        this.passwordFeedback = result.feedback.suggestions || [];
      } else {
        this.passwordScore = 0;
        this.passwordFeedback = [];
      }
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const request: RegisterRequest = this.registerForm.value;
      if (!this.authService.getRole())
        this.registerUser(request);
      else
        this.registerCaUser(request);
    } else {
      this.registerForm.markAllAsTouched();
    }
  }

  registerUser(request: RegisterRequest): void {
    this.authService.register(request).subscribe({
      next: () => {
        this.toasterService.success("Registration successful! Please check your email to activate your account.");
      },
      error: (err) => {
        this.toasterService.error(err?.error?.message, "Failed to register");
      }
    });
  }

  registerCaUser(request: RegisterRequest): void {
    this.authService.registerCaUser(request).subscribe({
      next: () => {
        this.toasterService.success("Registration successful! Activation email is sent.");
      },
      error: (err) => {
        this.toasterService.error(err?.error?.message, "Failed to register");
      }
    });
  }



}
