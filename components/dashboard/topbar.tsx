"use client";

import { useAuthStore } from "@/lib/auth/token-store";
import { useLogout } from "@/features/auth/hooks/use-logout";
import { Button } from "@/components/ui/button";

const ROLE_LABELS: Record<string, string> = {
  ROLE_STUDENT: "Student",
  ROLE_TEACHER: "Teacher",
  ROLE_VICE_PRINCIPAL: "Vice Principal",
  ROLE_PRINCIPAL: "Principal",
};

export function DashboardTopbar() {
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();

  const roleLabel = user?.authorities
    .filter((a) => a.startsWith("ROLE_"))
    .map((a) => ROLE_LABELS[a] ?? a)
    .join(", ");

  return (
    <header className="flex items-center justify-between border-b bg-background px-6 py-3">
      <div className="text-sm text-muted-foreground">{roleLabel}</div>
      <div className="flex items-center gap-3">
        <span className="text-sm font-medium">{user?.username}</span>
        <Button variant="outline" size="sm" onClick={logout}>
          Log out
        </Button>
      </div>
    </header>
  );
}