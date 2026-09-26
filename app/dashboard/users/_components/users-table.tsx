"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
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

interface UsersTableProps {
  users: UserResponse[];
  canActOnUsers: boolean;
  canManage: boolean;
  canManageRoles: boolean;
  onChangeStatus: (user: UserResponse, status: UserStatus) => void;
  onEditRoles: (user: UserResponse) => void;
  statusPendingId?: number;
}

function formatDate(value: string | null) {
  if (!value) return "—";
  return new Date(value).toLocaleDateString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export function UsersTable({
  users,
  canActOnUsers,
  canManage,
  canManageRoles,
  onChangeStatus,
  onEditRoles,
  statusPendingId,
}: UsersTableProps) {
  return (
    <div className="hidden md:block">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Username</TableHead>
            <TableHead>Email</TableHead>
            <TableHead>Roles</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Created</TableHead>
            {canActOnUsers && <TableHead className="text-right">Actions</TableHead>}
          </TableRow>
        </TableHeader>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell className="font-medium">
                <Link
                  href={`/dashboard/users/${user.id}`}
                  className="underline-offset-4 hover:underline"
                >
                  {user.username}
                </Link>
              </TableCell>
              <TableCell>{user.email ?? "—"}</TableCell>
              <TableCell>
                <div className="flex flex-wrap gap-1">
                  {user.roles.length === 0 ? (
                    <span className="text-muted-foreground">—</span>
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
              </TableCell>
              <TableCell>
                <UserStatusBadge status={user.status} />
              </TableCell>
              <TableCell>{formatDate(user.createdAt)}</TableCell>
              {canActOnUsers && (
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    {canManageRoles && (
                      <Button variant="outline" size="sm" onClick={() => onEditRoles(user)}>
                        Roles
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
                          {user.status === "PENDING" && (
                            <SelectItem value="PENDING" disabled>
                              Pending
                            </SelectItem>
                          )}
                        </SelectContent>
                      </Select>
                    )}
                  </div>
                </TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}