import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
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
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    TranslocoPipe,
  ],
  templateUrl: './user-roles-dialog.html',
})
export class UserRolesDialog {
  private readonly dialogRef = inject(
    MatDialogRef<UserRolesDialog, UserRolesDialogResult | undefined>
  );
  protected readonly data = inject<UserRolesDialogData>(MAT_DIALOG_DATA);

  protected readonly UserRoles = Object.values(UserRoleEnum) as UserRole[];
  protected readonly rolesControl = new FormControl<UserRole[]>([...this.data.user.roles], {
    nonNullable: true,
  });

  protected isOnlySelected(role: UserRole): boolean {
    const current = this.rolesControl.value;
    return current.length === 1 && current[0] === role;
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }

  protected save(): void {
    this.dialogRef.close({ roles: this.rolesControl.value });
  }
}
