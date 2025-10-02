import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function passwordMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password');
    const confirmPassword = control.get('passwordConfirmation');

    if (!password || !confirmPassword) return null; 
    
    const mismatch = password.value !== confirmPassword.value;
    if (mismatch) 
      confirmPassword.setErrors({ passwordMismatch: true });
    else 
      confirmPassword.setErrors(null);
    
    return mismatch ? { passwordMismatch: true } : null;
  };
}
  