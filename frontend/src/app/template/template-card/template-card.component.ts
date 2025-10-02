import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CertificateTemplate } from '../model/certificate-template.model';

@Component({
  selector: 'app-template-card',
  templateUrl: './template-card.component.html',
  standalone: false,
  styleUrls: ['./template-card.component.css']
})
export class TemplateCardComponent {
  @Input() template!: CertificateTemplate;
  @Output() edit = new EventEmitter<CertificateTemplate>();
  @Output() delete = new EventEmitter<CertificateTemplate>();
  @Output() use = new EventEmitter<CertificateTemplate>();
  @Input() issuer!: string;

  onCardClick(): void {
    this.use.emit(this.template);
  }

  useTemplate(): void {
    this.use.emit(this.template);
  }
}
