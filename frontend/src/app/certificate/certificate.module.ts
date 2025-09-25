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
import {FormsModule} from '@angular/forms';



@NgModule({
  declarations: [
    CertificateOverviewComponent,
    RootCertificateIssuanceComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,

  ]
})
export class CertificateModule { }
