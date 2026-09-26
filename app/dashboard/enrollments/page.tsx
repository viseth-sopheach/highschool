"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { useQueries } from "@tanstack/react-query";
import { useAuthorities, hasPermission, hasRole } from "@/lib/auth/roles";
import { useAcademicYears } from "@/features/academic-years/hooks/use-academic-years";
import { useSchoolClasses } from "@/features/school-classes/hooks/use-school-classes";
import { useClassEnrollmentsList } from "@/features/enrollments/hooks/use-class-enrollments-list";
import { useStudentEnrollments } from "@/features/enrollments/hooks/use-student-enrollments";
import { useChangeEnrollmentStatus } from "@/features/enrollments/hooks/use-change-enrollment-status";
import { useMyStudent } from "@/features/students/hooks/use-my-student";
import { getStudent } from "@/features/students/api/get-student";
import type { StudentResponse } from "@/features/students/types";
import { ENROLLMENT_STATUSES, type EnrollmentStatus } from "@/features/enrollments/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { EnrollmentFormDialog } from "./_components/enrollment-form-dialog";

const PAGE_SIZE = 20;

const STATUS_LABEL: Record<EnrollmentStatus, string> = {
  ACTIVE: "Active",
  TRANSFERRED: "Transferred",
  WITHDRAWN: "Withdrawn",
  GRADUATED: "Graduated",
};

const STATUS_BADGE: Record<EnrollmentStatus, string> = {
  ACTIVE:
    "border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-300",
  TRANSFERRED:
    "border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300",
  WITHDRAWN:
    "border-red-200 bg-red-50 text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300",
  GRADUATED:
    "border-sky-200 bg-sky-50 text-sky-700 dark:border-sky-900 dark:bg-sky-950/40 dark:text-sky-300",
};

function StatusBadge({ status }: { status: EnrollmentStatus }) {
  return (
    <span
      className={`inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium ${STATUS_BADGE[status]}`}
    >
      {STATUS_LABEL[status]}
    </span>
  );
}

export default function EnrollmentsPage() {
  const authorities = useAuthorities();
  const canReadAny = hasPermission(authorities, "ENROLLMENT_READ");
  const canWrite = hasPermission(authorities, "ENROLLMENT_WRITE");
  const isStudent = hasRole(authorities, "STUDENT");

  if (canReadAny) {
    return <StaffEnrollmentsView canWrite={canWrite} />;
  }
  if (isStudent) {
    return <MyEnrollmentsView />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Enrollments</CardTitle>
        <CardDescription>You don&apos;t have permission to view enrollments.</CardDescription>
      </CardHeader>
    </Card>
  );
}

function StaffEnrollmentsView({ canWrite }: { canWrite: boolean }) {
  const yearsQuery = useAcademicYears();
  const currentYearId = yearsQuery.data?.find((y) => y.current)?.id;

  const [academicYearId, setAcademicYearId] = useState<string>("");
  const [classValue, setClassValue] = useState("");
  const [status, setStatus] = useState<"all" | EnrollmentStatus>("all");
  const [page, setPage] = useState(0);
  const [formOpen, setFormOpen] = useState(false);

  useEffect(() => {
    if (!academicYearId && currentYearId) setAcademicYearId(String(currentYearId));
  }, [academicYearId, currentYearId]);

  const classesQuery = useSchoolClasses({
    page: 0,
    size: 100,
    academicYearId: academicYearId ? Number(academicYearId) : undefined,
  });
  const classes = classesQuery.data?.content ?? [];

  const selectedClassId =
    classValue && classes.some((c) => String(c.id) === classValue)
      ? classValue
      : (classes[0] ? String(classes[0].id) : "");
  const selectedClass = classes.find((c) => String(c.id) === selectedClassId);

  useEffect(() => {
    setPage(0);
  }, [selectedClassId, status]);

  const enrollmentsQuery = useClassEnrollmentsList(
    selectedClassId ? Number(selectedClassId) : undefined,
    { page, size: PAGE_SIZE },
  );
  const enrollments = enrollmentsQuery.data?.content ?? [];
  const filtered = status === "all" ? enrollments : enrollments.filter((e) => e.status === status);

  const studentQueries = useQueries({
    queries: enrollments.map((enrollment) => ({
      queryKey: ["students", "detail", enrollment.studentId],
      queryFn: () => getStudent(enrollment.studentId),
      staleTime: 5 * 60 * 1000,
    })),
    combine: (results) => ({
      byId: Object.fromEntries(
        results.flatMap((r) => (r.data ? [[r.data.id, r.data] as const] : [])),
      ) as Record<number, StudentResponse>,
    }),
  });

  const changeStatusMutation = useChangeEnrollmentStatus();
  const totalPages = enrollmentsQuery.data?.totalPages ?? 0;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Enrollments</h1>
          <p className="text-sm text-muted-foreground">
            Manage which class each student is enrolled in.
          </p>
        </div>
        {canWrite && (
          <Button onClick={() => setFormOpen(true)} disabled={classes.length === 0}>
            Enroll student
          </Button>
        )}
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <Select value={academicYearId} onValueChange={(v) => { setAcademicYearId(v); setClassValue(""); }}>
              <SelectTrigger className="w-full sm:w-56">
                <SelectValue placeholder="Select an academic year" />
              </SelectTrigger>
              <SelectContent>
                {yearsQuery.data?.map((year) => (
                  <SelectItem key={year.id} value={String(year.id)}>
                    {year.name}
                    {year.current ? " (current)" : ""}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Select value={selectedClassId} onValueChange={setClassValue} disabled={classes.length === 0}>
              <SelectTrigger className="w-full sm:w-64">
                <SelectValue placeholder="Select a class" />
              </SelectTrigger>
              <SelectContent>
                {classes.map((c) => (
                  <SelectItem key={c.id} value={String(c.id)}>
                    {c.name} · {c.gradeName}
                    {c.studyTrackName ? ` · ${c.studyTrackName}` : ""}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Select value={status} onValueChange={(v) => setStatus(v as typeof status)}>
              <SelectTrigger className="w-full sm:w-44">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All statuses</SelectItem>
                {ENROLLMENT_STATUSES.map((s) => (
                  <SelectItem key={s} value={s}>
                    {STATUS_LABEL[s]}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Student ID</TableHead>
                <TableHead>Student name</TableHead>
                <TableHead>Class</TableHead>
                <TableHead>Enrollment date</TableHead>
                <TableHead>Status</TableHead>
                {canWrite && <TableHead className="text-right">Actions</TableHead>}
              </TableRow>
            </TableHeader>
            <TableBody>
              {classesQuery.isLoading || yearsQuery.isLoading ? (
                <TableRow>
                  <TableCell colSpan={canWrite ? 6 : 5} className="py-8 text-center text-muted-foreground">
                    Loading…
                  </TableCell>
                </TableRow>
              ) : classes.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={canWrite ? 6 : 5} className="py-8 text-center text-muted-foreground">
                    No classes found for this academic year.
                  </TableCell>
                </TableRow>
              ) : enrollmentsQuery.isLoading ? (
                <TableRow>
                  <TableCell colSpan={canWrite ? 6 : 5} className="py-8 text-center text-muted-foreground">
                    Loading enrollments…
                  </TableCell>
                </TableRow>
              ) : enrollmentsQuery.isError ? (
                <TableRow>
                  <TableCell colSpan={canWrite ? 6 : 5} className="py-8 text-center text-destructive">
                    {toApiError(enrollmentsQuery.error).detail}
                  </TableCell>
                </TableRow>
              ) : filtered.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={canWrite ? 6 : 5} className="py-8 text-center text-muted-foreground">
                    No enrollments found for this class.
                  </TableCell>
                </TableRow>
              ) : (
                filtered.map((enrollment) => {
                  const student = studentQueries.byId[enrollment.studentId];
                  return (
                    <TableRow key={enrollment.id}>
                      <TableCell className="font-medium">
                        <Link
                          href={`/dashboard/students/${enrollment.studentId}`}
                          className="underline-offset-4 hover:underline"
                        >
                          {enrollment.studentCode}
                        </Link>
                      </TableCell>
                      <TableCell>{student?.khmerName ?? "—"}</TableCell>
                      <TableCell>{enrollment.schoolClassName}</TableCell>
                      <TableCell>{enrollment.enrollmentDate}</TableCell>
                      <TableCell>
                        {canWrite ? (
                          <Select
                            value={enrollment.status}
                            onValueChange={(value) =>
                              changeStatusMutation.mutate(
                                { id: enrollment.id, status: value as EnrollmentStatus },
                                { onError: (error) => toast.error(toApiError(error).detail) },
                              )
                            }
                          >
                            <SelectTrigger className="h-7 w-36 text-xs">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              {ENROLLMENT_STATUSES.map((s) => (
                                <SelectItem key={s} value={s}>
                                  {STATUS_LABEL[s]}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        ) : (
                          <StatusBadge status={enrollment.status} />
                        )}
                      </TableCell>
                      {canWrite && <TableCell className="text-right" />}
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>

          <div className="flex items-center justify-between pt-2">
            <p className="text-sm text-muted-foreground">
              {enrollmentsQuery.data
                ? `${enrollmentsQuery.data.totalElements} enrollment${enrollmentsQuery.data.totalElements === 1 ? "" : "s"}`
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
                disabled={enrollmentsQuery.data?.last ?? true}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {canWrite && (
        <EnrollmentFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          classes={classes}
          defaultClassId={selectedClass?.id}
        />
      )}
    </div>
  );
}

function MyEnrollmentsView() {
  const [page, setPage] = useState(0);
  const myStudent = useMyStudent();
  const enrollmentsQuery = useStudentEnrollments(myStudent.data?.id, { page, size: PAGE_SIZE });
  const enrollments = enrollmentsQuery.data?.content ?? [];
  const totalPages = enrollmentsQuery.data?.totalPages ?? 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">My enrollments</h1>
        <p className="text-sm text-muted-foreground">Your class history across academic years.</p>
      </div>

      <Card>
        <CardContent className="space-y-4 pt-6">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Academic year</TableHead>
                <TableHead>Class</TableHead>
                <TableHead>Enrollment date</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {myStudent.isLoading || enrollmentsQuery.isLoading ? (
                <TableRow>
                  <TableCell colSpan={4} className="py-8 text-center text-muted-foreground">
                    Loading…
                  </TableCell>
                </TableRow>
              ) : myStudent.isError ? (
                <TableRow>
                  <TableCell colSpan={4} className="py-8 text-center text-destructive">
                    {toApiError(myStudent.error).detail}
                  </TableCell>
                </TableRow>
              ) : enrollmentsQuery.isError ? (
                <TableRow>
                  <TableCell colSpan={4} className="py-8 text-center text-destructive">
                    {toApiError(enrollmentsQuery.error).detail}
                  </TableCell>
                </TableRow>
              ) : enrollments.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="py-8 text-center text-muted-foreground">
                    No enrollment history yet.
                  </TableCell>
                </TableRow>
              ) : (
                enrollments.map((enrollment) => (
                  <TableRow key={enrollment.id}>
                    <TableCell>{enrollment.academicYearName}</TableCell>
                    <TableCell className="font-medium">{enrollment.schoolClassName}</TableCell>
                    <TableCell>{enrollment.enrollmentDate}</TableCell>
                    <TableCell>
                      <StatusBadge status={enrollment.status} />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>

          <div className="flex items-center justify-end gap-2 pt-2">
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
              disabled={enrollmentsQuery.data?.last ?? true}
              onClick={() => setPage((p) => p + 1)}
            >
              Next
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}