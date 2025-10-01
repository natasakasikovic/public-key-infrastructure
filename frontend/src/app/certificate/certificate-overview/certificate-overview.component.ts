import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {CertificateService} from '../certificate.service';
import {MatTableDataSource} from '@angular/material/table';
import {CertificateResponse} from '../models/certificate-response.model';
import {Router} from '@angular/router';
import {PagedResponse} from '../../shared/model/paged-response';
import { RevokeDialogComponent } from '../revoke-dialog/revoke-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { RevocationResponse } from '../models/revocation-response.model';
import { HttpErrorResponse } from '@angular/common/http';

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
    private dialog: MatDialog,
    private toaster: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadCertificates(0, this.pageSize);
  }

  loadCertificates(pageIndex: number, pageSize: number): void {
    this.certificateService.getAll(pageIndex, pageSize).subscribe({
      next: (response: PagedResponse<CertificateResponse>) => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.loadCertificates(event.pageIndex, event.pageSize);
  }

  viewDetails(certificate: CertificateResponse): void {
    void this.router.navigate(["certificate", certificate.id]);
  }

  revokeCertificate(cert: CertificateResponse): void {
    const dialogRef = this.dialog.open(RevokeDialogComponent, {
    width: '400px',
    data: { certId: cert.id }
    });

    dialogRef.afterClosed().subscribe(reason => {
      if (reason) {
        this.certificateService.revokeCertificate(cert.id, { reason }).subscribe({
          next: (response: RevocationResponse) => {
            cert.status = 'REVOKED';
            this.dataSource._updateChangeSubscription();
            this.loadCertificates(this.paginator.pageIndex, this.paginator.pageSize);
            this.toaster.success('Certificate revoked successfully. Reason: ' + response.revocationReason, 'Success');
          },
          error: (error: HttpErrorResponse) => {
            this.toaster.error(error.error?.message, 'Error')
          }
        });
      }
    })
  }
}
