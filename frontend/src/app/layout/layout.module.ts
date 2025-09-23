import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { RouterLink } from '@angular/router';
import { MatToolbar } from '@angular/material/toolbar';
import { MaterialModule } from "../infrastructure/material/material.module";



@NgModule({
  declarations: [
    NavBarComponent
  ],
  exports: [
    NavBarComponent
  ],
  imports: [
    CommonModule,
    RouterLink,
    MaterialModule,
    MatToolbar
  ]
})
export class LayoutModule { }