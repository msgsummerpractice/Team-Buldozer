import { Component, inject } from '@angular/core';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { RolesAssignedDirective } from '@core/authorization/directives/role-assigned.directive';

@Component({
  selector: 'app-home',
  imports: [TranslocoPipe, RolesAssignedDirective],
  templateUrl: './home.html',
})
export class Home {
  protected auth = inject(AuthenticationService);
}
