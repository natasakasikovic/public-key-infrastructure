import {Component, OnInit} from '@angular/core';
import {CertificateDetails} from '../models/certificate-details-response.model';
import {switchMap} from 'rxjs';
import {CertificateService} from '../certificate.service';
import {ActivatedRoute} from '@angular/router';

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

  }
}
