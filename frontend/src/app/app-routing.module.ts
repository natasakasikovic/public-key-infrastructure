import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import {LoginComponent} from './auth/login/login.component';
import {CreateTemplateComponent} from './certificates/create-template/create-template.component';
import {TemplateOverviewComponent} from './certificates/template-overview/template-overview.component';
import {EditTemplateComponent} from './certificates/edit-template/edit-template.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'create-template', component: CreateTemplateComponent }, // TODO: allow only CA users
  { path: 'edit-template/:id', component: EditTemplateComponent }, // TODO: allow only CA users
  { path: 'templates', component: TemplateOverviewComponent }, // TODO: allow only CA users
  { path: '', redirectTo: 'register', pathMatch: 'full' },
  { path: '**', redirectTo: 'register', pathMatch: 'full' }, // TODO: change to login or error page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
