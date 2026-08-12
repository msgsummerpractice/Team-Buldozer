import { UserRole } from '@core/users/model/user-role';
import { UserLocation } from '@core/users/model/user-location';

export type UserResponse = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  location: UserLocation;
  status: boolean;
  roles: UserRole[];
};
