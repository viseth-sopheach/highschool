"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useStudents } from "@/features/students/hooks/use-students";
import { useDeleteStudent } from "@/features/students/hooks/use-delete-student";
import type { StudentResponse } from "@/features/students/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { StudentFormDialog } from "./_components/student-form-dialog";

const PAGE_SIZE = 20;

export default function StudentsPage() {
  const authorities = useAuthorities();
  const canRead = hasPermission(authorities, "STUDENT_READ");
  const canWrite = hasPermission(authorities, "STUDENT_WRITE");

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

  const studentsQuery = useStudents({
    page,
    size: PAGE_SIZE,
    search: debouncedSearch || undefined,
  });

  const deleteMutation = useDeleteStudent();

  const [formOpen, setFormOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<
    StudentResponse | undefined
  >();
  const [deletingStudent, setDeletingStudent] = useState<
    StudentResponse | undefined
  >();

  if (!canRead) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Students</CardTitle>
          <CardDescription>
            You don&apos;t have permission to view students.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const students = studentsQuery.data?.content ?? [];
  const totalPages = studentsQuery.data?.totalPages ?? 0;
  const columnCount = canWrite ? 7 : 6;

  const handleDelete = () => {
    if (!deletingStudent) return;
    deleteMutation.mutate(deletingStudent.id, {
      onSuccess: () => {
        toast.success(`Student "${deletingStudent.studentCode}" deleted.`);
        setDeletingStudent(undefined);
      },
      onError: (error) => {
        toast.error(toApiError(error).detail);
        setDeletingStudent(undefined);
      },
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Students</h1>
          <p className="text-sm text-muted-foreground">
            Manage student profiles and linked accounts.
          </p>
        </div>
        {canWrite && (
          <Button
            onClick={() => {
              setEditingStudent(undefined);
              setFormOpen(true);
            }}
          >
            New student
          </Button>
        )}
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <Input
              placeholder="Search by name or student code…"
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
                <TableHead>Gender</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Account</TableHead>
                {canWrite && (
                  <TableHead className="text-right">Actions</TableHead>
                )}
              </TableRow>
            </TableHeader>
            <TableBody>
              {studentsQuery.isLoading ? (
                <TableRow>
                  <TableCell
                    colSpan={columnCount}
                    className="py-8 text-center text-muted-foreground"
                  >
                    Loading students…
                  </TableCell>
                </TableRow>
              ) : studentsQuery.isError ? (
                <TableRow>
                  <TableCell
                    colSpan={columnCount}
                    className="py-8 text-center text-destructive"
                  >
                    {toApiError(studentsQuery.error).detail}
                  </TableCell>
                </TableRow>
              ) : students.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={columnCount}
                    className="py-8 text-center text-muted-foreground"
                  >
                    No students found.
                  </TableCell>
                </TableRow>
              ) : (
                students.map((student) => (
                  <TableRow key={student.id}>
                    <TableCell className="font-medium">
                      <Link
                        href={`/dashboard/students/${student.id}`}
                        className="underline-offset-4 hover:underline"
                      >
                        {student.studentCode}
                      </Link>
                    </TableCell>
                    <TableCell>{student.khmerName}</TableCell>
                    <TableCell>{student.englishName ?? "—"}</TableCell>
                    <TableCell>{student.gender ?? "—"}</TableCell>
                    <TableCell>{student.phone ?? "—"}</TableCell>
                    <TableCell>{student.username}</TableCell>
                    {canWrite && (
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setEditingStudent(student);
                              setFormOpen(true);
                            }}
                          >
                            Edit
                          </Button>
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => setDeletingStudent(student)}
                          >
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
              {studentsQuery.data
                ? `${studentsQuery.data.totalElements} student${studentsQuery.data.totalElements === 1 ? "" : "s"}`
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
                disabled={studentsQuery.data?.last ?? true}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {canWrite && (
        <StudentFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          student={editingStudent}
        />
      )}

      <Dialog
        open={Boolean(deletingStudent)}
        onOpenChange={(open) => !open && setDeletingStudent(undefined)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete student</DialogTitle>
            <DialogDescription>
              This will permanently delete student &quot;
              {deletingStudent?.studentCode}&quot; ({deletingStudent?.khmerName}
              ). This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setDeletingStudent(undefined)}
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={handleDelete}
              disabled={deleteMutation.isPending}
            >
              {deleteMutation.isPending ? "Deleting…" : "Delete"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
