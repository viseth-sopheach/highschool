"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import type { UserResponse, UserStatus } from "@/features/users/types";
import { UserStatusBadge } from "./user-status-badge";

const SETTABLE_STATUSES: UserStatus[] = ["ACTIVE", "LOCKED", "DISABLED"];

interface UsersMobileListProps {
  users: UserResponse[];
  canManage: boolean;
  canManageRoles: boolean;
  onChangeStatus: (user: UserResponse, status: UserStatus) => void;
  onEditRoles: (user: UserResponse) => void;
  statusPendingId?: number;
}

export function UsersMobileList({
  users,
  canManage,
  canManageRoles,
  onChangeStatus,
  onEditRoles,
  statusPendingId,
}: UsersMobileListProps) {
  return (
    <ul className="space-y-2 md:hidden">
      {users.map((user) => (
        <li key={user.id} className="space-y-3 rounded-lg border p-3">
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">
              <Link
                href={`/dashboard/users/${user.id}`}
                className="block truncate text-sm font-medium underline-offset-4 hover:underline"
              >
                {user.username}
              </Link>
              <p className="truncate text-xs text-muted-foreground">{user.email ?? "No email"}</p>
            </div>
            <UserStatusBadge status={user.status} />
          </div>

          <div className="flex flex-wrap gap-1">
            {user.roles.length === 0 ? (
              <span className="text-xs text-muted-foreground">No roles</span>
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

          {(canManage || canManageRoles) && (
            <div className="flex flex-wrap items-center gap-2 pt-1">
              {canManageRoles && (
                <Button variant="outline" size="sm" onClick={() => onEditRoles(user)}>
                  Edit roles
                </Button>
              )}
              {canManage && (
                <Select
                  value={user.status}
                  onValueChange={(value) => onChangeStatus(user, value as UserStatus)}
                  disabled={statusPendingId === user.id}
                >
                  <SelectTrigger className="h-8 w-32 text-xs">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {SETTABLE_STATUSES.map((status) => (
                      <SelectItem key={status} value={status}>
                        {status.charAt(0) + status.slice(1).toLowerCase()}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            </div>
          )}
        </li>
      ))}
    </ul>
  );
}