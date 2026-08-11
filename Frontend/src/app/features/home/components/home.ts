import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AuthenticationDirective } from 'core/authentication/directives/authentication.directive';
import { AuthenticationService } from 'core/authentication/services/authentication.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    AuthenticationDirective,
    RouterLink,
    CommonModule,
  ],
  templateUrl: './home.html',
})
export class Home {
  protected authentification = inject(AuthenticationService);

  logout() {
    this.authentification.logout();
  }
}
