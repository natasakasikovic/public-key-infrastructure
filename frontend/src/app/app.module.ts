import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutModule } from './layout/layout.module';
import { AuthModule } from './auth/auth.module';
import {HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi} from '@angular/common/http';
import {CertificatesModule} from './certificates/certificates.module';
import {AuthInterceptor} from './auth/auth.interceptor';
import {ToastrModule} from 'ngx-toastr';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {SharedModule} from './shared/shared.module';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ToastrModule.forRoot({
      timeOut: 1000,
      extendedTimeOut: 500,
      easeTime: 200,
    }),
    AppRoutingModule,
    LayoutModule,
    CertificatesModule,
    SharedModule,
    AuthModule
  ],
  providers: [
    provideAnimationsAsync(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
