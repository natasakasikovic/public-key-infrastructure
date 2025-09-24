import { Component, OnInit } from '@angular/core';
import { CreateRootCertificateRequest } from '../models/CreateRootCertificate.model';

@Component({
  standalone: false,
  selector: 'app-root-certificate-issuance',
  templateUrl: './root-certificate-issuance.component.html',
  styleUrls: ['./root-certificate-issuance.component.css'],
})
export class RootCertificateIssuanceComponent implements OnInit {
  ngOnInit(): void {
    // TODO: implement
  }

  rootCertDTO: CreateRootCertificateRequest = {
    startDate: '',
    endDate: '',
    keyUsages: [] as string[],
    extendedKeyUsages: [] as string[],
  };
}
