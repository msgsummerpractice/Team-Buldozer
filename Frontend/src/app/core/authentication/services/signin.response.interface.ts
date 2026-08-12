export interface SignInResponse {
  token: string | null;
  roles: string[] | null;
  message: string;
}
