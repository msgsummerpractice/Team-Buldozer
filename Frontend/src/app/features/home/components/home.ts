import { Component, inject } from '@angular/core';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { RolesAssignedDirective } from '@core/authorization/directives/role-assigned.directive';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { UserRoleEnum } from '@core/users/model/user-role';

@Component({
  selector: 'app-home',
  imports: [TranslocoPipe, RolesAssignedDirective, RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  protected auth = inject(AuthenticationService);
  protected readonly roles = UserRoleEnum;
}
