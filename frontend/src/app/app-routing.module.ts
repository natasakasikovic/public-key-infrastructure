import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import { RootCertificateIssuanceComponent } from './certificate/root-certificate-issuance/root-certificate-issuance.component';
import { LoginComponent } from './auth/login/login.component';
import { AuthGuard } from './auth/auth.guard';
import { SubordinateCertificateIssuanceComponent } from './certificate/subordinate-certificate-issuance/subordinate-certificate-issuance.component';
import { CertificateOverviewComponent } from './certificate/certificate-overview/certificate-overview.component';
import { CreateTemplateComponent } from './template/create-template/create-template.component';
import { EditTemplateComponent } from './template/edit-template/edit-template.component';
import { TemplateOverviewComponent } from './template/template-overview/template-overview.component';
import { CertificateDetailsComponent } from './certificate/certificate-details/certificate-details.component';
import { CaCertificateIssuanceComponent } from './certificate/ca-certificate-issuance/ca-certificate-issuance.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'home',
    component: CertificateOverviewComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER', 'ADMIN', 'REGULAR_USER'] },
  },
  {
    path: 'certificate/:id',
    component: CertificateDetailsComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER', 'ADMIN', 'REGULAR_USER'] },
  },
  {
    path: 'create-template',
    component: CreateTemplateComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] },
  },
  {
    path: 'edit-template/:id',
    component: EditTemplateComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] },
  },
  {
    path: 'templates',
    component: TemplateOverviewComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] },
  },
  {
    path: 'root-certificate-issuance',
    component: RootCertificateIssuanceComponent,
    canActivate: [AuthGuard],
    data: { role: ['ADMIN'] },
  },
  {
    path: 'subordinate-certificate-issuance',
    component: SubordinateCertificateIssuanceComponent,
    canActivate: [AuthGuard],
    data: { role: ['ADMIN'] },
  },
  {
    path: 'ca-certificate-issuance',
    component: CaCertificateIssuanceComponent,
    canActivate: [AuthGuard],
    data: { role: ['CA_USER'] },
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
