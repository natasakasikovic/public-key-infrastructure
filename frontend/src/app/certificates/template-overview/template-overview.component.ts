import {Component, OnInit } from '@angular/core';
import {CertificateTemplate} from '../model/certificate-template.model';
import {TemplateService} from '../template.service';

@Component({
  selector: 'app-template-overview',
  standalone: false,
  templateUrl: './template-overview.component.html',
  styleUrl: './template-overview.component.css'
})
export class TemplateOverviewComponent implements OnInit {
  templates: CertificateTemplate[] = [];

  constructor(private templateService: TemplateService) {
  }

  ngOnInit(): void {
    this.templateService.getTemplates().subscribe({
      next: (templates: CertificateTemplate[]) => {
        this.templates = templates;
      }
    });
  }

  editTemplate(t: CertificateTemplate) { /* edit logic */ }
  deleteTemplate(t: CertificateTemplate) { /* delete logic */ }
  useTemplate(t: CertificateTemplate) { /* create cert from template */ }
  duplicateTemplate(t: CertificateTemplate) { /* duplicate logic */ }
}
