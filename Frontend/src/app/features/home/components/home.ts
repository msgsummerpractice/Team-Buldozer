import { Component, inject } from '@angular/core';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [TranslocoPipe],
  templateUrl: './home.html',
})
export class Home {
  protected authentification = inject(AuthenticationService);
}
