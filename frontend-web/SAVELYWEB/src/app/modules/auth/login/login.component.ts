import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
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
          this.router.navigate(['/accueil']);
        }, 2200);
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