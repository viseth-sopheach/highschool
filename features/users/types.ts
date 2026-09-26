// Mirrors com.seth.backend.user.dto.* and com.seth.backend.user.entity.UserStatus exactly.
export const USER_STATUSES = ["ACTIVE", "LOCKED", "DISABLED", "PENDING"] as const;
export type UserStatus = (typeof USER_STATUSES)[number];

// Mirrors the fixed role catalog seeded in V1 (roles.name) — not user-manageable via API.
export const USER_ROLE_NAMES = ["STUDENT", "TEACHER", "VICE_PRINCIPAL", "PRINCIPAL"] as const;
export type UserRoleName = (typeof USER_ROLE_NAMES)[number];

export interface UserResponse {
  id: number;
  username: string;
  email: string | null;
  status: UserStatus;
  lastLoginAt: string | null; // ISO datetime
  createdAt: string; // ISO datetime
  roles: string[];
}

export interface UserCreateRequest {
  username: string;
  email?: string | null;
  password: string;
  roleNames: string[];
}

export interface UserStatusUpdateRequest {
  status: UserStatus;
}

export interface UserRoleAssignRequest {
  roleNames: string[];
}

export interface UserListParams {
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
}