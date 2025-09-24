import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TemplateFormComponent } from './template-form/template-form.component';
import {ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatChip, MatChipListbox, MatChipOption} from '@angular/material/chips';
import {MatCard} from '@angular/material/card';
import { TemplateOverviewComponent } from './template-overview/template-overview.component';
import {MatTooltip} from '@angular/material/tooltip';
import {MatIcon} from '@angular/material/icon';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef, MatTable
} from '@angular/material/table';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatToolbar} from '@angular/material/toolbar';
import { TemplateCardComponent } from './template-card/template-card.component';
import {RouterLink} from '@angular/router';



@NgModule({
  declarations: [
    TemplateFormComponent,
    TemplateOverviewComponent,
    TemplateCardComponent
  ],
  imports: [
    CommonModule,
    MatLabel,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatError,
    MatFormField,
    MatButton,
    MatChip,
    MatTooltip,
    MatIconButton,
    MatIcon,
    MatCard,
    MatRow,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatPaginator,
    MatMenu,
    MatMenuItem,
    MatCell,
    MatHeaderCell,
    MatColumnDef,
    MatTable,
    MatSort,
    MatHeaderCellDef,
    MatCellDef,
    MatMenuTrigger,
    MatPaginator,
    MatToolbar,
    MatChipListbox,
    MatChipOption,
    RouterLink,
  ]
})
export class CertificatesModule { }
