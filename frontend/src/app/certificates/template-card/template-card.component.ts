import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CertificateTemplate} from '../model/certificate-template.model';

@Component({
  selector: 'app-template-card',
  standalone: false,
  templateUrl: './template-card.component.html',
  styleUrl: './template-card.component.css'
})
export class TemplateCardComponent {
  @Input() template!: CertificateTemplate;
  @Output() edit = new EventEmitter<CertificateTemplate>();
  @Output() delete = new EventEmitter<CertificateTemplate>();
  @Output() use = new EventEmitter<CertificateTemplate>();

  shortLabel(text?: string): string {
    if (!text) return '';
    const max = 30;
    return text.length > max ? text.slice(0, max - 1) + '…' : text;
  }

  onCardClick(): void {
    this.use.emit(this.template);
  }

  useTemplate(): void {
    this.use.emit(this.template);
  }
}
