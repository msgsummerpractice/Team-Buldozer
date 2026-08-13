export interface JwtInterface {
  sub: string;
  roles: string[];
  exp: number;
  iat: number;
  userId: number;
}
