import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateTemplateComponent } from './create-template/create-template.component';
import { ReactiveFormsModule } from '@angular/forms';
import { TemplateOverviewComponent } from './template-overview/template-overview.component';
import { TemplateCardComponent } from './template-card/template-card.component';
import { RouterLink } from '@angular/router';
import { EditTemplateComponent } from './edit-template/edit-template.component';
import { MaterialModule } from '../infrastructure/material/material.module';
import { SubordinateCertificateIssuanceComponent } from './subordinate-certificate-issuance/subordinate-certificate-issuance.component';



@NgModule({
  declarations: [
    CreateTemplateComponent,
    TemplateOverviewComponent,
    TemplateCardComponent,
    EditTemplateComponent,
    SubordinateCertificateIssuanceComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    RouterLink,
  ]
})
export class CertificatesModule { }
