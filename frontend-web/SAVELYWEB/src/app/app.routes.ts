import { Routes } from '@angular/router';
import { Sidebar } from './disposition/sidebar/sidebar';
import { Dashboard } from './modules/superviseur/composants/dashboard/dashboard';
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
      { path: 'accueil', component: Dashboard },
      { path: 'agences', component: GestionAgenceZoneComponent },
      { path: 'clients', component: ListeClientsComponent },
      { path: 'collecteurs', component: ListeEmployesComponent },
      { path: 'caissiers', component: ListeEmployesComponent },
      { path: 'superviseurs', component: ListeEmployesComponent },
      { path: 'parametres', loadComponent: () => import('./modules/superviseur/composants/parametres/parametres').then(m => m.ParametresComponent) },
      { path: 'transactions', component: ListeTransactionsComponent },
      { path: 'reporting', loadComponent: () => import('./modules/superviseur/composants/reporting/reporting').then(m => m.ReportingComponent) },
      { path: 'profil', loadComponent: () => import('./modules/auth/profil/profil.component').then(m => m.ProfilComponent) },
      { path: '', redirectTo: 'accueil', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '' }
];