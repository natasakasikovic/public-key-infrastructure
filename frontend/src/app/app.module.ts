import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutModule } from './layout/layout.module';
import { AuthModule } from './auth/auth.module';
import { provideHttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RootCertificateIssuanceComponent } from './certificate/root-certificate-issuance/root-certificate-issuance.component';
import { CertificateOverviewComponent } from './certificate/certificate-overview/certificate-overview.component';
import {CertificateModule} from './certificate/certificate.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LayoutModule,
    AuthModule,
    CertificateModule,
    FormsModule,
  ],
  providers: [provideHttpClient()],
  bootstrap: [AppComponent],
})
export class AppModule {}
