import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService) {}

  ngOnInit() {
    const refresh$ = this.auth.tryRestoreSession();
    if (refresh$) {
      refresh$.subscribe({
        next: () => console.log('Session restored'),
        error: () => this.auth.clearTokens()
      });
    }
  }
}
