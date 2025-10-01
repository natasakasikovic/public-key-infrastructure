import { Component, Inject } from '@angular/core';
import { REVOCATION_REASONS } from '../../shared/constants/certificate-options';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-revoke-dialog',
  standalone: false,
  templateUrl: './revoke-dialog.component.html',
  styleUrl: './revoke-dialog.component.css'
})
export class RevokeDialogComponent {
  reasons = REVOCATION_REASONS;
  selectedReason: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<RevokeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { certId: string }
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.selectedReason) {
      this.dialogRef.close(this.selectedReason);
    }
  }
}
