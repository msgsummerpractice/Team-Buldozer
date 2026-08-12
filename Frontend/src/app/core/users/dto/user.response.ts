import {Role} from '@core/users/model/role';
import {Location} from '@core/users/model/location';

export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  location: Location;
  status: boolean;
  roles: Role[];
}
