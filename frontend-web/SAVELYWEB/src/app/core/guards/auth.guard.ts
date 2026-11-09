import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    // If route defines roles, verify user has one of them
    const roles: string[] = route.data['roles'];
    if (roles && roles.length > 0) {
      const hasAny = roles.some(r => this.authService.hasRole(r));
      if (!hasAny) {
        // Optionally navigate to unauthorized page or show message
        this.router.navigate(['/']);
        return false;
      }
    }

    return true;
  }
}
