"use client";

import { useMemo } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useQueries } from "@tanstack/react-query";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useSchoolClass } from "@/features/school-classes/hooks/use-school-class";
import { useClassEnrollments } from "@/features/enrollments/hooks/use-class-enrollments";
import { getStudent } from "@/features/students/api/get-student";
import type { StudentResponse } from "@/features/students/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
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

function Field({ label, value }: { label: string; value: string | null | undefined }) {
  return (
    <div>
      <div className="text-sm font-medium">{label}</div>
      <div className="text-sm text-muted-foreground">{value || "—"}</div>
    </div>
  );
}

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: "Active",
  TRANSFERRED: "Transferred",
  WITHDRAWN: "Withdrawn",
  GRADUATED: "Graduated",
};

export default function ClassDetailPage() {
  const params = useParams<{ classId: string }>();
  const classId = Number(params.classId);
  const isValidId = Number.isFinite(classId) && classId > 0;

  const authorities = useAuthorities();
  const canView = hasPermission(authorities, "CLASS_READ");
  const shouldQuery = canView && isValidId;

  const classQuery = useSchoolClass(classId, shouldQuery);
  const enrollmentsQuery = useClassEnrollments(shouldQuery ? classId : undefined);

  const enrollments = enrollmentsQuery.data?.content ?? [];
  // Only show students who are still meaningfully part of the roster.
  const rosterEnrollments = useMemo(
    () =>
      [...enrollments].sort((a, b) =>
        a.studentCode.localeCompare(b.studentCode, undefined, { numeric: true }),
      ),
    [enrollments],
  );

  const studentQueries = useQueries({
    queries: rosterEnrollments.map((enrollment) => ({
      queryKey: ["students", "detail", enrollment.studentId],
      queryFn: () => getStudent(enrollment.studentId),
      enabled: shouldQuery,
      staleTime: 5 * 60 * 1000,
    })),
    combine: (results) => ({
      byId: Object.fromEntries(
        results.flatMap((r) => (r.data ? [[r.data.id, r.data] as const] : [])),
      ) as Record<number, StudentResponse>,
      isLoading: results.some((r) => r.isLoading),
    }),
  });

  const backButton = (
    <Button asChild variant="outline" size="sm">
      <Link href="/dashboard/classes">Back to classes</Link>
    </Button>
  );

  if (!isValidId) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Class not found</CardTitle>
          <CardDescription>The class ID in the URL is invalid.</CardDescription>
        </CardHeader>
        <CardContent>{backButton}</CardContent>
      </Card>
    );
  }

  if (!canView) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Class</CardTitle>
          <CardDescription>You don&apos;t have permission to view this class.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  if (classQuery.isLoading) {
    return <p className="text-sm text-muted-foreground">Loading class…</p>;
  }

  if (classQuery.isError || !classQuery.data) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Class unavailable</CardTitle>
          <CardDescription>
            {classQuery.error ? toApiError(classQuery.error).detail : "Class not found."}
          </CardDescription>
        </CardHeader>
        <CardContent>{backButton}</CardContent>
      </Card>
    );
  }

  const schoolClass = classQuery.data;
  const studentCount = rosterEnrollments.length;
  const rosterLoading = enrollmentsQuery.isLoading || studentQueries.isLoading;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{schoolClass.name}</h1>
          <p className="text-sm text-muted-foreground">
            {schoolClass.gradeName}
            {schoolClass.studyTrackName ? ` · ${schoolClass.studyTrackName}` : ""} ·{" "}
            {schoolClass.academicYearName}
          </p>
        </div>
        {backButton}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Class information</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <Field label="Class name" value={schoolClass.name} />
          <Field label="Grade" value={schoolClass.gradeName} />
          <Field label="Study track" value={schoolClass.studyTrackName} />
          <Field label="Academic year" value={schoolClass.academicYearName} />
          <Field
            label="Capacity"
            value={schoolClass.capacity ? String(schoolClass.capacity) : null}
          />
          <Field label="Students" value={rosterLoading ? "—" : String(studentCount)} />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Students</CardTitle>
          <CardDescription>
            {rosterLoading ? "Loading…" : `Students: ${studentCount}`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {rosterLoading ? (
            <div className="space-y-2" role="status" aria-live="polite">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={i} className="h-11 animate-pulse rounded-lg bg-muted/60" />
              ))}
              <span className="sr-only">Loading students…</span>
            </div>
          ) : enrollmentsQuery.isError ? (
            <p className="py-8 text-center text-sm text-destructive">
              {toApiError(enrollmentsQuery.error).detail}
            </p>
          ) : rosterEnrollments.length === 0 ? (
            <p className="py-8 text-center text-sm text-muted-foreground">
              No students are enrolled in this class.
            </p>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Student ID</TableHead>
                    <TableHead>Student name</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Gender</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {rosterEnrollments.map((enrollment) => {
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
                        <TableCell>{student?.username ?? "—"}</TableCell>
                        <TableCell>{student?.gender ?? "—"}</TableCell>
                        <TableCell>
                          {STATUS_LABEL[enrollment.status] ?? enrollment.status}
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}