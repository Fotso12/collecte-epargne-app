import { Routes } from '@angular/router';
import { Sidebar } from './disposition/sidebar/sidebar';
import { Dashboard } from './modules/superviseur/composants/dashboard/dashboard';
import { CaissierDashboard } from './modules/caissier/composants/dashboard/dashboard';
import { CaissierValidations } from './modules/caissier/composants/validations/validations';
import { AdminDashboard } from './modules/admin/composants/admin-dashboard/admin-dashboard';
import { ListeClientsComponent } from './modules/superviseur/composants/Liste-clients/liste-clients';
import { ListeEmployesComponent } from './modules/superviseur/composants/Liste-employes/liste-employes';
import { LoginComponent } from './modules/auth/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { ListeTransactionsComponent } from './modules/superviseur/composants/Liste-transactions/liste-transactions';
import { GestionAgenceZoneComponent } from './modules/superviseur/composants/gestion-agence-zone/gestion-agence-zone';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: Sidebar,
    canActivate: [AuthGuard],
    children: [
      { path: 'accueil', component: Dashboard, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'caissier/accueil', component: CaissierDashboard, canActivate: [AuthGuard], data: { roles: ['CAISSIER'] } },
      { path: 'caissier/validations', component: CaissierValidations, canActivate: [AuthGuard], data: { roles: ['CAISSIER'] } },
      { path: 'agences', component: GestionAgenceZoneComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'clients', component: ListeClientsComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'collecteurs', component: ListeEmployesComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'caissiers', component: ListeEmployesComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'superviseurs', component: ListeEmployesComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'parametres', loadComponent: () => import('./modules/superviseur/composants/parametres/parametres').then(m => m.ParametresComponent) },
      { path: 'admin', component: AdminDashboard, canActivate: [AuthGuard], data: { roles: ['ADMIN','SUPERADMIN'] } },
      { path: 'transactions', component: ListeTransactionsComponent, canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'superviseur/validation-comptes', loadComponent: () => import('./modules/superviseur/composants/validation-comptes/validation-comptes').then(m => m.ValidationComptesComponent), canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR'] } },
      { path: 'superviseur/recu', loadComponent: () => import('./modules/superviseur/composants/recu/recu').then(m => m.RecuComponent), canActivate: [AuthGuard], data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'caissier/recu', loadComponent: () => import('./modules/superviseur/composants/recu/recu').then(m => m.RecuComponent), canActivate: [AuthGuard], data: { roles: ['CAISSIER'] } },
      { path: 'reporting', loadComponent: () => import('./modules/superviseur/composants/reporting/reporting').then(m => m.ReportingComponent), data: { roles: ['SUPERVISEUR','ADMIN'] } },
      { path: 'profil', loadComponent: () => import('./modules/auth/profil/profil.component').then(m => m.ProfilComponent) },
      { path: '', redirectTo: 'accueil', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '' }
];