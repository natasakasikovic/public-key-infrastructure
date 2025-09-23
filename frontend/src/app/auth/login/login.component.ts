import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../auth.service';
import {LoginResponse} from '../model/login-response.model';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });
  serverError: string | null = null;

  constructor(private authService: AuthService) {}

  onLogin(): void {
    if(this.loginForm.invalid) return;
    this.authService.login(this.loginForm.value).subscribe({
      next: (response: LoginResponse) => {
        this.authService.setTokens(response);
      },
      error: (error: HttpErrorResponse) => {
        this.serverError = error?.error?.message;
      }
    });
  }
}
