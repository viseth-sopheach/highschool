"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useAuthorities, hasPermission } from "@/lib/auth/roles";
import { useTeacher } from "@/features/teachers/hooks/use-teacher";
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

export default function TeacherDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const authorities = useAuthorities();
  const canView = hasPermission(authorities, "TEACHER_READ");

  const teacherQuery = useTeacher(id, canView);

  if (!canView) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Teacher</CardTitle>
          <CardDescription>You don&apos;t have permission to view this teacher.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  const backButton = (
    <Button asChild variant="outline" size="sm">
      <Link href="/dashboard/teachers">Back to teachers</Link>
    </Button>
  );

  if (teacherQuery.isLoading) {
    return <p className="text-sm text-muted-foreground">Loading teacher…</p>;
  }

  if (teacherQuery.isError || !teacherQuery.data) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Teacher unavailable</CardTitle>
          <CardDescription>
            {teacherQuery.error ? toApiError(teacherQuery.error).detail : "Teacher not found."}
          </CardDescription>
        </CardHeader>
        <CardContent>{backButton}</CardContent>
      </Card>
    );
  }

  const teacher = teacherQuery.data;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{teacher.khmerName}</h1>
          <p className="text-sm text-muted-foreground">{teacher.teacherCode}</p>
        </div>
        {backButton}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Profile</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <Field label="Khmer name" value={teacher.khmerName} />
          <Field label="English name" value={teacher.englishName} />
          <Field label="Phone" value={teacher.phone} />
          <Field label="Hire date" value={teacher.hireDate} />
          <Field label="Account" value={teacher.username} />
        </CardContent>
      </Card>
    </div>
  );
}