import { Routes } from '@angular/router';
// Notice we import from './login' not './login.component'
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { LandlordDashboardComponent } from './components/landlord-dashboard/landlord-dashboard';
import { TenantDashboardComponent } from './components/tenant-dashboard/tenant-dashboard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'landlord-dashboard', component: LandlordDashboardComponent },
  { path: 'tenant-dashboard', component: TenantDashboardComponent }
];