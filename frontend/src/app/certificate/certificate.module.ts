import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MaterialModule} from '../infrastructure/material/material.module';
import {CertificateOverviewComponent} from './certificate-overview/certificate-overview.component';
import {RootCertificateIssuanceComponent} from './root-certificate-issuance/root-certificate-issuance.component';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatRow, MatTable
} from '@angular/material/table';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CertificateDetailsComponent } from './certificate-details/certificate-details.component';
import {RouterLink} from '@angular/router';
import { RevokeDialogComponent } from './revoke-dialog/revoke-dialog.component';



@NgModule({
  declarations: [
    CertificateOverviewComponent,
    RootCertificateIssuanceComponent,
    CertificateDetailsComponent,
    RevokeDialogComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,

  ]
})
export class CertificateModule { }
