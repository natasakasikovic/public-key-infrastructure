import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';
import {catchError, Observable, of, switchMap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private authService: AuthService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.authService.tryRestoreSession().pipe(
      switchMap(() => {
        const userRole: string | null = this.authService.getRole();
        const requiredRoles: string[] = route.data['role'];

        if (!userRole) {
          void this.router.navigate(['login']);
          return of(false);
        }

        if (!requiredRoles.some(role => userRole === role)) {
          void this.router.navigate(['home']);
          return of(false);
        }

        return of(true);
      }),
      catchError(() => {
        this.authService.clearTokens();
        void this.router.navigate(['login']);
        return of(false);
      })
    );
  }
}
