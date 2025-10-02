import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function regexValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) return null;
    try {
      new RegExp(value);
      return null;
    } catch (e) {
      return { invalidRegex: true };
    }
  };
}
