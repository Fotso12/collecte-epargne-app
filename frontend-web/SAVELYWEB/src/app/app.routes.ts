import { Routes } from '@angular/router';
import { Sidebar } from './disposition/sidebar/sidebar';
import { Dashboard } from './modules/superviseur/composants/dashboard/dashboard';
import { ListeClientsComponent } from './modules/superviseur/composants/Liste-clients/liste-clients';
import { ListeEmployesComponent } from './modules/superviseur/composants/Liste-employes/liste-employes';

export const routes: Routes = [
  {
    path: '',
    component: Sidebar,
    children: [
      { path: 'accueil', component: Dashboard },
      { path: 'clients', component: ListeClientsComponent },
      // Nouvelles routes pour le personnel
      { path: 'collecteurs', component: ListeEmployesComponent },
      { path: 'caissiers', component: ListeEmployesComponent },
      
      { path: '', redirectTo: 'accueil', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'accueil' }
];