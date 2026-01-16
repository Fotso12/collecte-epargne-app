import { Routes } from '@angular/router';
import { Sidebar } from './disposition/sidebar/sidebar';
import { Dashboard } from './modules/superviseur/composants/dashboard/dashboard';
import { ListeClientsComponent } from './modules/superviseur/composants/Liste-clients/liste-clients';
import { ListeEmployesComponent } from './modules/superviseur/composants/Liste-employes/liste-employes';
import { LoginComponent } from './modules/auth/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  { path: 'forgot-password', loadComponent: () =>
      import('./modules/auth/forgot-password/forgot-password.component')
        .then(m => m.ForgotPasswordComponent)
  },

  { path: 'verify-code', loadComponent: () =>
      import('./modules/auth/verify-code/verify-code.component')
        .then(m => m.VerifyCodeComponent)
  },

  { path: 'reset-password', loadComponent: () =>
      import('./modules/auth/reset-password/reset-password.component')
        .then(m => m.ResetPasswordComponent)
  },

  {
    path: '',
    component: Sidebar,
    canActivate: [AuthGuard],
    children: [
      { path: 'accueil', component: Dashboard },
      { path: 'clients', component: ListeClientsComponent },
      { path: 'collecteurs', component: ListeEmployesComponent },
      { path: 'caissiers', component: ListeEmployesComponent },
      { path: '', redirectTo: 'accueil', pathMatch: 'full' }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
