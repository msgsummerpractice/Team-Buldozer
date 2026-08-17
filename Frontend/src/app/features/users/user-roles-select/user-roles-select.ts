import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';
import { UserResponse } from '@core/users/dto/user.response';
import { UserRole, UserRoleEnum } from '@core/users/model/user-role';

export type UserRolesDialogData = { user: UserResponse };
export type UserRolesDialogResult = { roles: UserRole[] };

@Component({
  selector: 'app-user-roles-select',
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    TranslocoPipe,
  ],
  templateUrl: './user-roles-select.html',
})
export class UserRolesDialog {
  private readonly dialogRef = inject(
    MatDialogRef<UserRolesDialog, UserRolesDialogResult | undefined>
  );
  protected readonly data = inject<UserRolesDialogData>(MAT_DIALOG_DATA);

  protected readonly UserRoles = Object.values(UserRoleEnum) as UserRole[];
  protected selectedRoles: UserRole[] = [...this.data.user.roles];

  protected isOnlySelected(role: UserRole): boolean {
    return this.selectedRoles.length === 1 && this.selectedRoles[0] === role;
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }

  protected save(): void {
    this.dialogRef.close({ roles: this.selectedRoles });
  }
}
