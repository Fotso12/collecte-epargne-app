import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PasswordService } from '../../../core/services/password.service';

@Component({
  standalone: true,
  selector: 'app-verify-code',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './verify-code.component.html',
  styleUrls: ['./verify-code.component.css']
})
export class VerifyCodeComponent implements OnInit {

  form: FormGroup;
  email = '';
  error = '';
  success = ''; // Ajout pour le message de confirmation
  loading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordService: PasswordService
  ) {
    this.form = this.fb.group({
      code: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.email = params.get('email') ?? '';
      if (!this.email) {
        this.router.navigate(['/forgot-password']);
      }
    });
  }

  submit() {
    if (this.form.invalid || !this.email) {
      this.error = "Données invalides. Veuillez recommencer.";
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const codeSaisi = this.form.value.code;

    this.passwordService.verifyCode(this.email, codeSaisi).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Code validé avec succès ! Redirection...';

        // Redirection après 1.5 seconde pour laisser l'utilisateur lire le succès
        setTimeout(() => {
          this.router.navigate(['/reset-password'], {
            queryParams: { email: this.email }
          });
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        // On récupère le message d'erreur du backend (ex: "Code expiré")
        this.error = err.error?.error || 'Code invalide ou expiré';
        console.error('Détails erreur 400:', err);
      }
    });
  }
}
