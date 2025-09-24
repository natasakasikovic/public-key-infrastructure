import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import {LoginComponent} from './auth/login/login.component';
import {TemplateFormComponent} from './certificates/template-form/template-form.component';
import {TemplateOverviewComponent} from './certificates/template-overview/template-overview.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'create-template', component: TemplateFormComponent }, // TODO: allow only CA users
  { path: 'templates', component: TemplateOverviewComponent }, // TODO: allow only CA users
  { path: '', redirectTo: 'register', pathMatch: 'full' },
  { path: '**', redirectTo: 'register', pathMatch: 'full' }, // TODO: change to login or error page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
