export type PasswordStrength = 'Too short' | 'Weak' | 'Medium' | 'Strong';

// TODO: Ensure this password strength logic matches the backend validation rules
export function checkPasswordStrength(password: string): PasswordStrength {
  if (!password) return 'Too short';

  if (password.length < 8) return 'Too short';

  let score = 0;

  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;

  if (score < 2) return 'Weak';
  if (score === 2 || score === 3) return 'Medium';
  if (score === 4) return 'Strong';

  return 'Weak';
}
