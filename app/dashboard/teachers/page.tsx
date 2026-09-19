"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useTeachers } from "@/features/teachers/hooks/use-teachers";
import { useDeleteTeacher } from "@/features/teachers/hooks/use-delete-teacher";
import type { TeacherResponse } from "@/features/teachers/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { TeacherFormDialog } from "./_components/teacher-form-dialog";

const PAGE_SIZE = 20;

export default function TeachersPage() {
  const authorities = useAuthorities();
  const canRead = hasPermission(authorities, "TEACHER_READ");
  const canWrite = hasPermission(authorities, "TEACHER_WRITE");

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [page, setPage] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search.trim()), 300);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch]);

  const teachersQuery = useTeachers({
    page,
    size: PAGE_SIZE,
    search: debouncedSearch || undefined,
  });

  const deleteMutation = useDeleteTeacher();

  const [formOpen, setFormOpen] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState<TeacherResponse | undefined>();
  const [deletingTeacher, setDeletingTeacher] = useState<TeacherResponse | undefined>();

  if (!canRead) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Teachers</CardTitle>
          <CardDescription>You don&apos;t have permission to view teachers.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const teachers = teachersQuery.data?.content ?? [];
  const totalPages = teachersQuery.data?.totalPages ?? 0;
  const columnCount = canWrite ? 7 : 6;

  const handleDelete = () => {
    if (!deletingTeacher) return;
    deleteMutation.mutate(deletingTeacher.id, {
      onSuccess: () => {
        toast.success(`Teacher "${deletingTeacher.teacherCode}" deleted.`);
        setDeletingTeacher(undefined);
      },
      onError: (error) => {
        toast.error(toApiError(error).detail);
        setDeletingTeacher(undefined);
      },
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Teachers</h1>
          <p className="text-sm text-muted-foreground">Manage teacher profiles and linked accounts.</p>
        </div>
        {canWrite && (
          <Button
            onClick={() => {
              setEditingTeacher(undefined);
              setFormOpen(true);
            }}
          >
            New teacher
          </Button>
        )}
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <Input
              placeholder="Search by name or teacher code…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="sm:max-w-72"
            />
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Code</TableHead>
                <TableHead>Khmer name</TableHead>
                <TableHead>English name</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Hire date</TableHead>
                <TableHead>Account</TableHead>
                {canWrite && <TableHead className="text-right">Actions</TableHead>}
              </TableRow>
            </TableHeader>
            <TableBody>
              {teachersQuery.isLoading ? (
                <TableRow>
                  <TableCell colSpan={columnCount} className="py-8 text-center text-muted-foreground">
                    Loading teachers…
                  </TableCell>
                </TableRow>
              ) : teachers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={columnCount} className="py-8 text-center text-muted-foreground">
                    No teachers found.
                  </TableCell>
                </TableRow>
              ) : (
                teachers.map((teacher) => (
                  <TableRow key={teacher.id}>
                    <TableCell className="font-medium">
                      <Link
                        href={`/dashboard/teachers/${teacher.id}`}
                        className="underline-offset-4 hover:underline"
                      >
                        {teacher.teacherCode}
                      </Link>
                    </TableCell>
                    <TableCell>{teacher.khmerName}</TableCell>
                    <TableCell>{teacher.englishName ?? "—"}</TableCell>
                    <TableCell>{teacher.phone ?? "—"}</TableCell>
                    <TableCell>{teacher.hireDate ?? "—"}</TableCell>
                    <TableCell>{teacher.username}</TableCell>
                    {canWrite && (
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setEditingTeacher(teacher);
                              setFormOpen(true);
                            }}
                          >
                            Edit
                          </Button>
                          <Button variant="destructive" size="sm" onClick={() => setDeletingTeacher(teacher)}>
                            Delete
                          </Button>
                        </div>
                      </TableCell>
                    )}
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>

          <div className="flex items-center justify-between pt-2">
            <p className="text-sm text-muted-foreground">
              {teachersQuery.data
                ? `${teachersQuery.data.totalElements} teacher${teachersQuery.data.totalElements === 1 ? "" : "s"}`
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
                disabled={teachersQuery.data?.last ?? true}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {canWrite && (
        <TeacherFormDialog open={formOpen} onOpenChange={setFormOpen} teacher={editingTeacher} />
      )}

      <Dialog open={Boolean(deletingTeacher)} onOpenChange={(open) => !open && setDeletingTeacher(undefined)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete teacher</DialogTitle>
            <DialogDescription>
              This will permanently delete teacher &quot;{deletingTeacher?.teacherCode}&quot; (
              {deletingTeacher?.khmerName}). This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeletingTeacher(undefined)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleDelete} disabled={deleteMutation.isPending}>
              {deleteMutation.isPending ? "Deleting…" : "Delete"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}