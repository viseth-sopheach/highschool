"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useUsers } from "@/features/users/hooks/use-users";
import { useUpdateUserStatus } from "@/features/users/hooks/use-update-user-status";
import { useAssignUserRoles } from "@/features/users/hooks/use-assign-user-roles";
import { USER_STATUSES, USER_ROLE_NAMES } from "@/features/users/types";
import type { UserResponse, UserStatus, UserRoleName } from "@/features/users/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { UsersTable } from "./_components/users-table";
import { UsersMobileList } from "./_components/users-mobile-list";
import { UserCreateDialog } from "./_components/user-create-dialog";
import { UserRolesDialog } from "./_components/user-roles-dialog";

const PAGE_SIZE = 20;

const STATUS_LABEL: Record<UserStatus, string> = {
  ACTIVE: "Active",
  LOCKED: "Locked",
  DISABLED: "Disabled",
  PENDING: "Pending",
};

const ROLE_LABEL: Record<UserRoleName, string> = {
  STUDENT: "Student",
  TEACHER: "Teacher",
  VICE_PRINCIPAL: "Vice Principal",
  PRINCIPAL: "Principal",
};

const SORT_OPTIONS = [
  { value: "username,asc", label: "Username (A–Z)" },
  { value: "createdAt,desc", label: "Newest first" },
  { value: "createdAt,asc", label: "Oldest first" },
];

export default function UsersPage() {
  const authorities = useAuthorities();
  const canRead = hasPermission(authorities, "USER_READ");
  const canManage = hasPermission(authorities, "USER_MANAGE");
  const canManageRoles = hasPermission(authorities, "ROLE_MANAGE");
  const canActOnUsers = canManage || canManageRoles;

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState<"all" | UserRoleName>("all");
  const [statusFilter, setStatusFilter] = useState<"all" | UserStatus>("all");
  const [sort, setSort] = useState(SORT_OPTIONS[0].value);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search.trim()), 300);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, roleFilter, statusFilter, sort]);

  const usersQuery = useUsers({
    page,
    size: PAGE_SIZE,
    search: debouncedSearch || undefined,
    sort,
  });

  const statusMutation = useUpdateUserStatus();
  const rolesMutation = useAssignUserRoles();

  const [createOpen, setCreateOpen] = useState(false);
  const [rolesTarget, setRolesTarget] = useState<UserResponse | undefined>();
  const [statusTarget, setStatusTarget] = useState<
    { user: UserResponse; status: UserStatus } | undefined
  >();

  if (!canRead) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Users</CardTitle>
          <CardDescription>You don&apos;t have permission to view users.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const allUsers = usersQuery.data?.content ?? [];
  const users = allUsers.filter((user) => {
    if (roleFilter !== "all" && !user.roles.includes(roleFilter)) return false;
    if (statusFilter !== "all" && user.status !== statusFilter) return false;
    return true;
  });
  const totalPages = usersQuery.data?.totalPages ?? 0;
  const hasActiveFilters = roleFilter !== "all" || statusFilter !== "all";

  const handleStatusRequest = (user: UserResponse, status: UserStatus) => {
    if (status === user.status) return;
    if (status === "LOCKED" || status === "DISABLED") {
      setStatusTarget({ user, status });
      return;
    }
    statusMutation.mutate(
      { id: user.id, payload: { status } },
      {
        onSuccess: () =>
          toast.success(`"${user.username}" is now ${STATUS_LABEL[status].toLowerCase()}.`),
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  };

  const confirmStatusChange = () => {
    if (!statusTarget) return;
    const { user, status } = statusTarget;
    statusMutation.mutate(
      { id: user.id, payload: { status } },
      {
        onSuccess: () => {
          toast.success(`"${user.username}" is now ${STATUS_LABEL[status].toLowerCase()}.`);
          setStatusTarget(undefined);
        },
        onError: (error) => {
          toast.error(toApiError(error).detail);
          setStatusTarget(undefined);
        },
      },
    );
  };

  const handleSaveRoles = (roleNames: string[]) => {
    if (!rolesTarget) return;
    rolesMutation.mutate(
      { id: rolesTarget.id, payload: { roleNames } },
      {
        onSuccess: () => {
          toast.success(`Roles updated for "${rolesTarget.username}".`);
          setRolesTarget(undefined);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  };

  const resetFilters = () => {
    setRoleFilter("all");
    setStatusFilter("all");
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Users</h1>
          <p className="text-sm text-muted-foreground">
            Manage every account in the system, including roles and access status.
          </p>
        </div>
        {canManage && <Button onClick={() => setCreateOpen(true)}>New user</Button>}
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-[minmax(0,1fr)_10rem_10rem_12rem]">
            <Input
              placeholder="Search by username or email…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            <Select value={roleFilter} onValueChange={(v) => setRoleFilter(v as typeof roleFilter)}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="All roles" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All roles</SelectItem>
                {USER_ROLE_NAMES.map((role) => (
                  <SelectItem key={role} value={role}>
                    {ROLE_LABEL[role]}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select
              value={statusFilter}
              onValueChange={(v) => setStatusFilter(v as typeof statusFilter)}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="All statuses" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All statuses</SelectItem>
                {USER_STATUSES.map((status) => (
                  <SelectItem key={status} value={status}>
                    {STATUS_LABEL[status]}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select value={sort} onValueChange={setSort}>
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {SORT_OPTIONS.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {hasActiveFilters && (
            <Button type="button" variant="ghost" size="sm" onClick={resetFilters}>
              Clear filters
            </Button>
          )}

          {usersQuery.isLoading ? (
            <div className="space-y-2" role="status" aria-live="polite">
              {Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="h-11 animate-pulse rounded-lg bg-muted/60" />
              ))}
              <span className="sr-only">Loading users…</span>
            </div>
          ) : usersQuery.isError ? (
            <div
              role="alert"
              className="flex flex-col items-center justify-center gap-2 rounded-lg border border-destructive/30 bg-destructive/5 px-4 py-12 text-center"
            >
              <p className="text-sm font-medium text-destructive">Something went wrong</p>
              <p className="max-w-sm text-sm text-muted-foreground">
                {toApiError(usersQuery.error).detail}
              </p>
              <Button variant="outline" size="sm" onClick={() => void usersQuery.refetch()}>
                Try again
              </Button>
            </div>
          ) : users.length === 0 ? (
            <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed px-4 py-12 text-center">
              <p className="text-sm font-medium">
                {allUsers.length === 0 ? "No users found." : "No users match the current filters."}
              </p>
              <p className="max-w-sm text-sm text-muted-foreground">
                {allUsers.length === 0
                  ? "Once accounts are created, they will show up here."
                  : "Try adjusting your search or filters."}
              </p>
              {hasActiveFilters && allUsers.length > 0 && (
                <Button variant="outline" size="sm" onClick={resetFilters}>
                  Clear filters
                </Button>
              )}
            </div>
          ) : (
            <>
              <UsersTable
                users={users}
                canActOnUsers={canActOnUsers}
                canManage={canManage}
                canManageRoles={canManageRoles}
                onChangeStatus={handleStatusRequest}
                onEditRoles={setRolesTarget}
                statusPendingId={statusMutation.isPending ? statusMutation.variables?.id : undefined}
              />
              <UsersMobileList
                users={users}
                canManage={canManage}
                canManageRoles={canManageRoles}
                onChangeStatus={handleStatusRequest}
                onEditRoles={setRolesTarget}
                statusPendingId={statusMutation.isPending ? statusMutation.variables?.id : undefined}
              />
            </>
          )}

          <div className="flex items-center justify-between pt-2">
            <p className="text-sm text-muted-foreground">
              {usersQuery.data
                ? `${usersQuery.data.totalElements} user${usersQuery.data.totalElements === 1 ? "" : "s"}`
                : null}
            </p>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Previous
              </Button>
              <span className="text-sm text-muted-foreground">
                Page {totalPages === 0 ? 0 : page + 1} of {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={usersQuery.data?.last ?? true}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {canManage && <UserCreateDialog open={createOpen} onOpenChange={setCreateOpen} />}

      {canManageRoles && (
        <UserRolesDialog
          user={rolesTarget}
          pending={rolesMutation.isPending}
          onSave={handleSaveRoles}
          onClose={() => setRolesTarget(undefined)}
        />
      )}

      <Dialog open={Boolean(statusTarget)} onOpenChange={(open) => !open && setStatusTarget(undefined)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{statusTarget?.status === "DISABLED" ? "Disable" : "Lock"} account</DialogTitle>
            <DialogDescription>
              {statusTarget && (
                <>
                  This will set &quot;{statusTarget.user.username}&quot;&apos;s status to{" "}
                  {STATUS_LABEL[statusTarget.status].toLowerCase()}, preventing them from signing in
                  until it is changed back.
                </>
              )}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setStatusTarget(undefined)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={confirmStatusChange} disabled={statusMutation.isPending}>
              {statusMutation.isPending ? "Saving…" : "Confirm"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}