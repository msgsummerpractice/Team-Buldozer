import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';
import { UserResponse } from '@core/users/dto/user.response';
import { UserRole, UserRoleEnum } from '@core/users/model/user-role';

export type UserRolesDialogData = { user: UserResponse };
export type UserRolesDialogResult = { roles: UserRole[] };

@Component({
  selector: 'app-user-roles-dialog',
  imports: [
    MatDialogModule,
    MatCheckboxModule,
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
  protected readonly selectedRoles = signal<Set<UserRole>>(new Set(this.data.user.roles));

  protected isSelected(role: UserRole): boolean {
    return this.selectedRoles().has(role);
  }

  protected toggle(role: UserRole, checked: boolean): void {
    const next = new Set(this.selectedRoles());
    checked ? next.add(role) : next.delete(role);
    this.selectedRoles.set(next);
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }

  protected save(): void {
    this.dialogRef.close({ roles: [...this.selectedRoles()] });
  }
}
