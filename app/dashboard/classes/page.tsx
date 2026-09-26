"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useSchoolClasses } from "@/features/school-classes/hooks/use-school-classes";
import { useDeleteSchoolClass } from "@/features/school-classes/hooks/use-delete-school-class";
import { useAcademicYears } from "@/features/academic-years/hooks/use-academic-years";
import { useGrades } from "@/features/grades/hooks/use-grades";
import { useStudyTracks } from "@/features/study-tracks/hooks/use-study-tracks";
import type { SchoolClassResponse } from "@/features/school-classes/types";
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
import { ClassFormDialog } from "./_components/class-form-dialog";

const PAGE_SIZE = 20;

export default function ClassesPage() {
  const authorities = useAuthorities();
  const canRead = hasPermission(authorities, "CLASS_READ");
  const canWrite = hasPermission(authorities, "CLASS_WRITE");

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [academicYearId, setAcademicYearId] = useState("");
  const [gradeId, setGradeId] = useState("");
  const [page, setPage] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search.trim()), 300);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, academicYearId, gradeId]);

  const academicYearsQuery = useAcademicYears();
  const gradesQuery = useGrades();
  const studyTracksQuery = useStudyTracks();

  const classesQuery = useSchoolClasses({
    page,
    size: PAGE_SIZE,
    search: debouncedSearch || undefined,
    academicYearId: academicYearId ? Number(academicYearId) : undefined,
    gradeId: gradeId ? Number(gradeId) : undefined,
  });

  const deleteMutation = useDeleteSchoolClass();

  const [formOpen, setFormOpen] = useState(false);
  const [editingClass, setEditingClass] = useState<
    SchoolClassResponse | undefined
  >();
  const [deletingClass, setDeletingClass] = useState<
    SchoolClassResponse | undefined
  >();

  if (!canRead) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Classes</CardTitle>
          <CardDescription>
            You don&apos;t have permission to view classes.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const classes = classesQuery.data?.content ?? [];
  const totalPages = classesQuery.data?.totalPages ?? 0;
  const columnCount = canWrite ? 6 : 5;

  const handleDelete = () => {
    if (!deletingClass) return;
    deleteMutation.mutate(deletingClass.id, {
      onSuccess: () => {
        toast.success(`Class "${deletingClass.name}" deleted.`);
        setDeletingClass(undefined);
      },
      onError: (error) => {
        toast.error(toApiError(error).detail);
        setDeletingClass(undefined);
      },
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Classes</h1>
          <p className="text-sm text-muted-foreground">
            Manage classes across grades, study tracks, and academic years.
          </p>
        </div>
        {canWrite && (
          <Button
            onClick={() => {
              setEditingClass(undefined);
              setFormOpen(true);
            }}
          >
            New class
          </Button>
        )}
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <Input
              placeholder="Search by class name…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="sm:max-w-64"
            />
            <Select
              value={academicYearId}
              onValueChange={(v) => setAcademicYearId(v === "all" ? "" : v)}
            >
              <SelectTrigger className="w-full sm:w-48">
                <SelectValue placeholder="All academic years" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All academic years</SelectItem>
                {academicYearsQuery.data?.map((year) => (
                  <SelectItem key={year.id} value={String(year.id)}>
                    {year.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select
              value={gradeId}
              onValueChange={(v) => setGradeId(v === "all" ? "" : v)}
            >
              <SelectTrigger className="w-full sm:w-40">
                <SelectValue placeholder="All grades" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All grades</SelectItem>
                {gradesQuery.data?.map((grade) => (
                  <SelectItem key={grade.id} value={String(grade.id)}>
                    {grade.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Grade</TableHead>
                <TableHead>Track</TableHead>
                <TableHead>Academic year</TableHead>
                <TableHead>Capacity</TableHead>
                {canWrite && (
                  <TableHead className="text-right">Actions</TableHead>
                )}
              </TableRow>
            </TableHeader>
            <TableBody>
              {classesQuery.isLoading ? (
                <TableRow>
                  <TableCell
                    colSpan={columnCount}
                    className="py-8 text-center text-muted-foreground"
                  >
                    Loading classes…
                  </TableCell>
                </TableRow>
              ) : classes.length === 0 ? (
                <TableRow>
                  <TableCell
                    colSpan={columnCount}
                    className="py-8 text-center text-muted-foreground"
                  >
                    No classes found.
                  </TableCell>
                </TableRow>
              ) : (
                classes.map((schoolClass) => (
                  <TableRow key={schoolClass.id}>
                    <TableCell className="font-medium">
                      <Link
                        href={`/dashboard/classes/${schoolClass.id}`}
                        className="underline-offset-4 hover:underline"
                      >
                        {schoolClass.name}
                      </Link>
                    </TableCell>
                    <TableCell>{schoolClass.gradeName}</TableCell>
                    <TableCell>{schoolClass.studyTrackName ?? "—"}</TableCell>
                    <TableCell>{schoolClass.academicYearName}</TableCell>
                    <TableCell>{schoolClass.capacity ?? "—"}</TableCell>
                    {canWrite && (
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setEditingClass(schoolClass);
                              setFormOpen(true);
                            }}
                          >
                            Edit
                          </Button>
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => setDeletingClass(schoolClass)}
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
              {classesQuery.data
                ? `${classesQuery.data.totalElements} class${classesQuery.data.totalElements === 1 ? "" : "es"}`
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
                disabled={classesQuery.data?.last ?? true}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {canWrite && (
        <ClassFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          schoolClass={editingClass}
          academicYears={academicYearsQuery.data ?? []}
          grades={gradesQuery.data ?? []}
          studyTracks={studyTracksQuery.data ?? []}
        />
      )}

      <Dialog
        open={Boolean(deletingClass)}
        onOpenChange={(open) => !open && setDeletingClass(undefined)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete class</DialogTitle>
            <DialogDescription>
              This will permanently delete class &quot;{deletingClass?.name}
              &quot;. This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setDeletingClass(undefined)}
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
