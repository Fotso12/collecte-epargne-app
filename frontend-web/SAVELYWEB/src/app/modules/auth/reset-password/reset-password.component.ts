import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PasswordService } from '../../../core/services/password.service';

@Component({
  standalone: true,
  selector: 'app-reset-password',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  form: FormGroup;
  email: string = '';
  error: string = '';
  success: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordService: PasswordService
  ) {
    this.form = this.fb.group({
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/) // Exige 1 Majuscule et 1 Chiffre
        ]
      ],
      confirm: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Récupération de l'email depuis les paramètres de l'URL
    this.route.queryParamMap.subscribe(params => {
      const emailParam = params.get('email');
      if (emailParam) {
        this.email = emailParam.trim().toLowerCase();
        console.log('DEBUG - Email récupéré dans l\'URL :', this.email);
      } else {
        console.error('DEBUG - Aucun email trouvé dans l\'URL');
        this.error = "L'email est manquant. Veuillez recommencer la procédure.";
      }
    });
  }

  submit(): void {
    // 1. Logs de diagnostic pour voir ce qui est envoyé
    console.log('--- TENTATIVE DE RÉINITIALISATION ---');
    console.log('Email envoyé au backend :', `[${this.email}]`);
    console.log('Formulaire valide ?', this.form.valid);

    // 2. Vérification si l'email est présent
    if (!this.email) {
      this.error = "Session expirée ou email manquant. Veuillez redemander un code.";
      return;
    }

    // 3. Vérification de la validité du formulaire (Pattern Majuscule/Chiffre)
    if (this.form.invalid) {
      this.error = "Le mot de passe doit contenir au moins 8 caractères, une majuscule et un chiffre.";
      return;
    }

    // 4. Vérification de la correspondance
    if (this.form.value.password !== this.form.value.confirm) {
      this.error = "Les mots de passe ne correspondent pas.";
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    // 5. Appel au service
    this.passwordService.resetPassword(this.email, this.form.value.password).subscribe({
      next: (res) => {
        console.log('Réponse succès backend :', res);
        this.loading = false;
        this.success = "Mot de passe réinitialisé avec succès ! Redirection...";

        // Redirection après un petit délai
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        console.error('Réponse erreur backend :', err);
        this.loading = false;
        // Affiche l'erreur "Utilisateur introuvable" si le backend la renvoie
        this.error = err.error?.error || "Une erreur est survenue lors de la réinitialisation.";
      }
    });
  }
}
