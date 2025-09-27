import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';



@NgModule({
  declarations: [
    ConfirmationDialogComponent
  ],
  exports: [
    ConfirmationDialogComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
  ]
})
export class SharedModule { }
