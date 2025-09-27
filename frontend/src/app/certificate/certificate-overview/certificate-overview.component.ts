import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {CertificateService} from '../certificate.service';
import {MatTableDataSource} from '@angular/material/table';
import {CertificateResponse} from '../models/certificate-response.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-certificate-overview',
  standalone: false,
  templateUrl: './certificate-overview.component.html',
  styleUrl: './certificate-overview.component.css'
})
export class CertificateOverviewComponent implements OnInit {
  displayedColumns: string[] = ['serialNumber','certificateType','issuerMail','subjectMail','revoke','details'];
  dataSource = new MatTableDataSource<CertificateResponse>([]);

  totalElements = 0;
  pageSize = 10;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private certificateService: CertificateService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.loadCertificates(0, this.pageSize);
  }

  loadCertificates(pageIndex: number, pageSize: number) {
    this.certificateService.getAll(pageIndex, pageSize).subscribe(
      response => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
      }
    );
  }

  onPageChange(event: PageEvent) {
    this.loadCertificates(event.pageIndex, event.pageSize);
  }

  viewDetails(certificate: CertificateResponse): void {
    const id = certificate?.id;
    if (!id) {
      console.warn('Cannot navigate, certificate ID is undefined');
      return;
    }
    void this.router.navigate(["certificate", certificate.id])
  }

  revokeCertificate(cert: CertificateResponse): void {
    // TODO
  }
}
