"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useUser } from "@/features/users/hooks/use-user";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { UserStatusBadge } from "../_components/user-status-badge";

function Field({ label, value }: { label: string; value: string | null | undefined }) {
  return (
    <div>
      <div className="text-sm font-medium">{label}</div>
      <div className="text-sm text-muted-foreground">{value || "—"}</div>
    </div>
  );
}

function formatDateTime(value: string | null) {
  if (!value) return null;
  return new Date(value).toLocaleString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function UserDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const authorities = useAuthorities();
  const canView = hasPermission(authorities, "USER_READ");

  const userQuery = useUser(id, canView && Number.isFinite(id));

  if (!canView) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>User</CardTitle>
          <CardDescription>You don&apos;t have permission to view this user.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const backButton = (
    <Button asChild variant="outline" size="sm">
      <Link href="/dashboard/users">Back to users</Link>
    </Button>
  );

  if (userQuery.isLoading) {
    return <p className="text-sm text-muted-foreground">Loading user…</p>;
  }

  if (userQuery.isError || !userQuery.data) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>User unavailable</CardTitle>
          <CardDescription>
            {userQuery.error ? toApiError(userQuery.error).detail : "User not found."}
          </CardDescription>
        </CardHeader>
        <CardContent>{backButton}</CardContent>
      </Card>
    );
  }

  const user = userQuery.data;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{user.username}</h1>
          <p className="text-sm text-muted-foreground">{user.email ?? "No email on file"}</p>
        </div>
        {backButton}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Account</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="Username" value={user.username} />
          <Field label="Email" value={user.email} />
          <div>
            <div className="text-sm font-medium">Status</div>
            <div className="pt-1">
              <UserStatusBadge status={user.status} />
            </div>
          </div>
          <div>
            <div className="text-sm font-medium">Roles</div>
            <div className="flex flex-wrap gap-1 pt-1">
              {user.roles.length === 0 ? (
                <span className="text-sm text-muted-foreground">—</span>
              ) : (
                user.roles.map((role) => (
                  <span
                    key={role}
                    className="inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium text-muted-foreground"
                  >
                    {role.replaceAll("_", " ")}
                  </span>
                ))
              )}
            </div>
          </div>
          <Field label="Created" value={formatDateTime(user.createdAt)} />
          <Field label="Last login" value={formatDateTime(user.lastLoginAt)} />
        </CardContent>
      </Card>
    </div>
  );
}