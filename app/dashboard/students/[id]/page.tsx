"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useStudent } from "@/features/students/hooks/use-student";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

function Field({ label, value }: { label: string; value: string | null | undefined }) {
  return (
    <div>
      <div className="text-sm font-medium">{label}</div>
      <div className="text-sm text-muted-foreground">{value || "—"}</div>
    </div>
  );
}

export default function StudentDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const authorities = useAuthorities();
  const canView =
    hasPermission(authorities, "STUDENT_READ") || hasPermission(authorities, "STUDENT_READ_OWN");

  const studentQuery = useStudent(id, canView);

  if (!canView) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Student</CardTitle>
          <CardDescription>You don&apos;t have permission to view this student.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const backButton = (
    <Button asChild variant="outline" size="sm">
      <Link href="/dashboard/students">Back to students</Link>
    </Button>
  );

  if (studentQuery.isLoading) {
    return <p className="text-sm text-muted-foreground">Loading student…</p>;
  }

  if (studentQuery.isError || !studentQuery.data) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Student unavailable</CardTitle>
          <CardDescription>
            {studentQuery.error ? toApiError(studentQuery.error).detail : "Student not found."}
          </CardDescription>
        </CardHeader>
        <CardContent>{backButton}</CardContent>
      </Card>
    );
  }

  const student = studentQuery.data;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{student.khmerName}</h1>
          <p className="text-sm text-muted-foreground">{student.studentCode}</p>
        </div>
        {backButton}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Profile</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="Khmer name" value={student.khmerName} />
          <Field label="English name" value={student.englishName} />
          <Field label="Date of birth" value={student.dob} />
          <Field label="Gender" value={student.gender} />
          <Field label="Phone" value={student.phone} />
          <Field label="Account" value={student.username} />
        </CardContent>
      </Card>
    </div>
  );
}