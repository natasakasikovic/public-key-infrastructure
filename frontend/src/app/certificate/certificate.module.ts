import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { CertificateOverviewComponent } from './certificate-overview/certificate-overview.component';
import { RootCertificateIssuanceComponent } from './root-certificate-issuance/root-certificate-issuance.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CertificateDetailsComponent } from './certificate-details/certificate-details.component';
import {RouterLink} from '@angular/router';
import { RevokeDialogComponent } from './revoke-dialog/revoke-dialog.component';
import { CaCertificateIssuanceComponent } from './ca-certificate-issuance/ca-certificate-issuance.component';
import { CsrSelfUploadComponent } from './csr-self-upload/csr-self-upload.component';
import { CsrAutoGenerateComponent } from './csr-auto-generate/csr-auto-generate.component';


@NgModule({
  declarations: [
    CertificateOverviewComponent,
    RootCertificateIssuanceComponent,
    CertificateDetailsComponent,
    RevokeDialogComponent,
    CaCertificateIssuanceComponent,
    CsrSelfUploadComponent,
    CsrAutoGenerateComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
  ],
})
export class CertificateModule {}
