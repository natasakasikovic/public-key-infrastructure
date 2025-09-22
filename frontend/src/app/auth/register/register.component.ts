import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RegisterRequest } from '../model/register-request.model';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  standalone: false
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
      passwordConfirmation: ['', [Validators.required]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      organization: ['', Validators.required]
    }, { validators: this.passwordsMatch });
  }

  // TODO: this is not working properly, fix it
  passwordsMatch(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirm = group.get('passwordConfirmation')?.value;
    return password === confirm ? null : { passwordsMismatch: true };
  }

  onSubmit() {
    if (this.registerForm.valid) {
      console.log(this.registerForm.value);
      var request: RegisterRequest = this.registerForm.value;
      this.authService.register(request).subscribe({
        next: () => {
          console.log('Registration successful');
          // TODO: add dialog about successful registration and email confirmation
        },
        error: (err) => {
          console.error('Registration failed', err);
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }

}
