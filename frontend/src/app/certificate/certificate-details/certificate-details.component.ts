import {Component, OnInit} from '@angular/core';
import {CertificateDetails} from '../models/certificate-details-response.model';
import {switchMap} from 'rxjs';
import {CertificateService} from '../certificate.service';
import {ActivatedRoute} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-certificate-details',
  standalone: false,
  templateUrl: './certificate-details.component.html',
  styleUrl: './certificate-details.component.css'
})
export class CertificateDetailsComponent implements OnInit {
  data?: CertificateDetails;

  constructor(
    private certificateService: CertificateService,
    private route: ActivatedRoute,
    private toasterService: ToastrService,
  ) {}


  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap(paramMap => {
          const id = paramMap.get('id')!;
          return this.certificateService.getCertificate(id);
        })
      )
      .subscribe({
        next: (certificate: CertificateDetails) => {
          this.data = certificate;
        }
      });
  }

  onDownload(): void {
    if(!this.data) return;
    this.certificateService.downloadCertificate(this.data.serialNumber).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `cert-${this.data?.serialNumber}.p12`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.toasterService.error("Failed to download certificate. Please try again later.");
      }
    });
  }

  onCrlDownload(): void {
    if (!this.data) return;
    this.certificateService.getCrl(this.data.serialNumber).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a'); 
        a.href = url;
        a.download = `cert-${this.data?.serialNumber}.crl`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      }, 
      error: (err) => {
        this.toasterService.error("Failed to download CRL. Please try again later.");
      }
    });
  }
}
