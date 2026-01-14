import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PasswordService } from '../../../core/services/password.service';


@Component({
  standalone: true,
  selector: 'app-forgot-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {

  form: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private passwordService: PasswordService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.loading = true;
    this.error = '';

    const email = this.form.value.email;

    this.passwordService.forgotPassword(email).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/verify-code'], {
          queryParams: { email }
        });
      },
      error: (err) => {
        this.loading = false;
        console.error('Erreur lors de la demande de mot de passe oublié :', err);
        if (err.status === 0) {
          this.error = 'Impossible de contacter le serveur. Vérifiez votre connexion ou contactez le support.';
        } else {
          this.error = err.error?.error || 'Une erreur est survenue (Email introuvable ou erreur serveur).';
        }
      }
    });
  }
}
