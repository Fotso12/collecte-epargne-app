import { Routes } from '@angular/router';
import { Sidebar } from './disposition/sidebar/sidebar';
import { Dashboard } from './modules/superviseur/composants/dashboard/dashboard';
import { ListeClientsComponent } from './modules/superviseur/composants/Liste-clients/liste-clients';

export const routes: Routes = [
  {
    path: '',
    component: Sidebar, // Coquille fixe
    children: [
      { path: 'accueil', component: Dashboard }, // Page de stats
      { path: 'clients', component: ListeClientsComponent }, // Page liste
      { path: '', redirectTo: 'accueil', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'accueil' }
];