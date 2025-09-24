import {Component, OnInit } from '@angular/core';
import {CertificateTemplate} from '../model/certificate-template.model';
import {TemplateService} from '../template.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ConfirmationDialogComponent} from '../../shared/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-template-overview',
  standalone: false,
  templateUrl: './template-overview.component.html',
  styleUrl: './template-overview.component.css'
})
export class TemplateOverviewComponent implements OnInit {
  templates: CertificateTemplate[] = [];

  constructor(
    private templateService: TemplateService,
    private toasterService: ToastrService,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.templateService.getTemplates().subscribe({
      next: (templates: CertificateTemplate[]) => {
        this.templates = templates;
      }
    });
  }

  openDeleteConfirmation(template: CertificateTemplate): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { message: "Are you sure you want to delete " + template.name + "?"}
    });
    this.handleConfirmationDialogClose(dialogRef, template);
  }

  private handleConfirmationDialogClose(dialogRef: MatDialogRef<ConfirmationDialogComponent>, template: CertificateTemplate){
    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed)
        this.deleteTemplate(template);
    });
  }

  deleteTemplate(template: CertificateTemplate) {
    if (!template.id) return;
    this.templateService.deleteTemplate(template.id).subscribe({
      next: () => {
        this.toasterService.success("Template deleted successfully.")
        this.templates = this.templates.filter(t => template.id !== t.id);
      },
      error: () => this.toasterService.error("Failed to delete template."),
    });
  }
  useTemplate(template: CertificateTemplate) { /* create cert from template */ }
  duplicateTemplate(template: CertificateTemplate) { /* duplicate logic */ }

  editTemplate(template: CertificateTemplate) {
    void this.router.navigate(['edit-template', template.id]);
  }
}
