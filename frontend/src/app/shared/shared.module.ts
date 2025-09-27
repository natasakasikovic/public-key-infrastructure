import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ConfirmationDialogComponent} from './confirmation-dialog/confirmation-dialog.component';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';



@NgModule({
  declarations: [
    ConfirmationDialogComponent
  ],
  exports: [
    ConfirmationDialogComponent,
  ],
  imports: [
    CommonModule,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatDialogContent,
    MatDialogTitle
  ]
})
export class SharedModule { }
