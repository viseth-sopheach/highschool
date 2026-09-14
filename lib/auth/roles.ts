import { useAuthStore } from "@/lib/auth/token-store";

export type RoleName = "STUDENT" | "TEACHER" | "VICE_PRINCIPAL" | "PRINCIPAL";

/** Live authorities from the decoded access token, e.g. ["ROLE_TEACHER", "PERM_STUDENT_READ"]. */
export function useAuthorities(): string[] {
  return useAuthStore((s) => s.user?.authorities ?? []);
}

export function hasRole(authorities: string[], role: RoleName): boolean {
  return authorities.includes(`ROLE_${role}`);
}

export function hasPermission(authorities: string[], permission: string): boolean {
  return authorities.includes(`PERM_${permission}`);
}