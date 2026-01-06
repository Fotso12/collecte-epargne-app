import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css']
})
export class ProfilComponent implements OnInit {
  profileForm: FormGroup;
  passwordForm: FormGroup;
  user: any;
  message = '';
  isError = false;

  constructor(
    private authService: AuthService, 
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.user = this.authService.getUser();
    
    this.profileForm = this.fb.group({
      nom: [this.user?.nom || '', Validators.required],
      prenom: [this.user?.prenom || '', Validators.required],
      email: [this.user?.email || '', [Validators.required, Validators.email]]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(userData => {
      if (userData) {
        this.user = userData;
        this.cdr.detectChanges();
      }
    });
  }

  updateProfile() {
    if (this.profileForm.valid) {
      this.isError = false;
      const updatedUser = { ...this.user, ...this.profileForm.value };
      
      // Mise à jour locale via le service
      this.authService.updateLocalUserInfo(updatedUser);
      
      this.message = "Profil mis à jour avec succès !";
      this.cdr.detectChanges();
      
      setTimeout(() => this.message = '', 3000);
    }
  }

  updatePassword() {
    if (this.passwordForm.invalid) {
      this.isError = true;
      this.message = "Veuillez remplir correctement tous les champs.";
      return;
    }

    const { newPassword, confirmPassword } = this.passwordForm.value;

    if (newPassword !== confirmPassword) {
      this.isError = true;
      this.message = "La confirmation ne correspond pas au nouveau mot de passe !";
      this.cdr.detectChanges();
      return;
    }

    // APPEL RÉEL AU BACKEND (Remplace la simulation)
    this.authService.changePassword(newPassword).subscribe({
      next: (res) => {
        this.isError = false;
        this.message = "Votre mot de passe a été modifié avec succès !";
        this.passwordForm.reset();
        this.cdr.detectChanges();
        setTimeout(() => this.message = '', 4000);
      },
      error: (err) => {
        this.isError = true;
        this.message = "Erreur lors du changement de mot de passe.";
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }
}