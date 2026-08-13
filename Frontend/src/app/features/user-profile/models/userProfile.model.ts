export type UserLocation = 'CLUJ' | 'TIMISOARA' | 'MURES';
export type UserRole = 'PARTICIPANT' | 'MARKETING' | 'HR' | 'ADMIN';

export interface UserProfile {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	location: UserLocation;
	status: boolean;
	roles: UserRole[];
	profilePicture: string | null;
}
