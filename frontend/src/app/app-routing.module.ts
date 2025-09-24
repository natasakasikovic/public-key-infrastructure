import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import {LoginComponent} from './auth/login/login.component';
import {CreateTemplateComponent} from './certificates/create-template/create-template.component';
import {TemplateOverviewComponent} from './certificates/template-overview/template-overview.component';
import {EditTemplateComponent} from './certificates/edit-template/edit-template.component';
import {AuthGuard} from './auth/auth.guard';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'create-template',
    component: CreateTemplateComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] }
  },
  {
    path: 'edit-template/:id',
    component: EditTemplateComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] }
  },
  {
    path: 'templates',
    component: TemplateOverviewComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] }
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
