import type { RoleName } from "@/lib/auth/roles";

export interface NavItem {
  label: string;
  href: string;
  /** PERM_* authority required, e.g. "PERM_STUDENT_READ". */
  permission?: string;
  /** Fallback role gate for items with no dedicated permission (e.g. self-service views). */
  roles?: RoleName[];
}

// Mirrors com.seth.backend.security.PermissionConstants exactly — add a nav
// item here the moment a matching permission/endpoint exists on the backend.
export const navItems: NavItem[] = [
  { label: "Overview", href: "/dashboard" },
  { label: "My Profile", href: "/dashboard/profile", roles: ["STUDENT", "TEACHER"] },
  { label: "Students", href: "/dashboard/students", permission: "PERM_STUDENT_READ" },
  { label: "Teachers", href: "/dashboard/teachers", permission: "PERM_TEACHER_READ" },
  { label: "Classes", href: "/dashboard/classes", permission: "PERM_CLASS_READ" },
  { label: "Enrollments", href: "/dashboard/enrollments", permission: "PERM_ENROLLMENT_READ" },
  { label: "Attendance", href: "/dashboard/attendance", permission: "PERM_ATTENDANCE_READ" },
  { label: "My Attendance", href: "/dashboard/attendance/me", roles: ["STUDENT"] },
  { label: "Assessments", href: "/dashboard/assessments", permission: "PERM_ASSESSMENT_READ" },
  { label: "Scores", href: "/dashboard/scores", permission: "PERM_SCORE_READ" },
  { label: "My Scores", href: "/dashboard/scores/me", roles: ["STUDENT"] },
  { label: "Users", href: "/dashboard/users", permission: "PERM_USER_READ" },
  { label: "Audit Log", href: "/dashboard/audit", permission: "PERM_AUDIT_READ" },
];

export function visibleNavItems(authorities: string[]): NavItem[] {
  return navItems.filter((item) => {
    if (item.permission) return authorities.includes(item.permission);
    if (item.roles) return item.roles.some((r) => authorities.includes(`ROLE_${r}`));
    return true;
  });
}