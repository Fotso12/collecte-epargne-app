import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  error = '';
  showPassword = false;
  showSuccessModal = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Force la modale à être cachée au chargement initial
    this.showSuccessModal = false;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.error = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        
        // ÉTAPE CRUCIALE : On s'assure que la variable change d'état
        this.showSuccessModal = true;
        
        // On force Angular à rafraîchir le DOM immédiatement
        this.cdr.markForCheck(); 
        this.cdr.detectChanges(); 

        // Petit délai de sécurité pour laisser le navigateur dessiner
        setTimeout(() => {
          // Redirection basée sur le rôle principal
          if (this.authService.hasRole('CAISSIER')) {
            this.router.navigate(['/caissier/accueil']);
            return;
          }
          if (this.authService.hasRole('SUPERVISEUR')) {
            this.router.navigate(['/accueil']);
            return;
          }
          if (this.authService.hasRole('ADMIN') || this.authService.hasRole('SUPERADMIN')) {
            this.router.navigate(['/admin']);
            return;
          }
          // Default fallback
          this.router.navigate(['/accueil']);
        }, 1200);
      },
      error: (error) => {
        this.loading = false;
        this.error = 'Email ou mot de passe incorrect';
        this.showSuccessModal = false; // Sécurité
        this.cdr.detectChanges();
      }
    });
  }
}