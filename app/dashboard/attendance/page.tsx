"use client";

import { useMemo, useState, type ReactNode } from "react";
import { RefreshCwIcon } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { hasPermission, hasRole, useAuthorities } from "@/lib/auth/roles";
import { toApiError } from "@/lib/errors/api-error";
import { cn } from "@/lib/utils";
import { useAcademicYears } from "@/features/academic-years/hooks/use-academic-years";
import { useSchoolClasses } from "@/features/school-classes/hooks/use-school-classes";
import { useMyTeacher } from "@/features/teachers/hooks/use-my-teacher";
import { useTeacherAssignments } from "@/features/class-teacher-assignments/hooks/use-teacher-assignments";
import { useAttendanceRoster } from "@/features/attendance/hooks/use-attendance-roster";
import { useMarkAttendance } from "@/features/attendance/hooks/use-mark-attendance";
import { useMarkAttendanceBulk } from "@/features/attendance/hooks/use-mark-attendance-bulk";
import type {
  AttendanceRow,
  AttendanceStatus,
  AttendanceStatusFilter,
} from "@/features/attendance/types";
import { formatLongDate, summarizeAttendance, todayIso } from "@/features/attendance/utils";
import { AttendanceDetailDialog } from "./_components/attendance-detail-dialog";
import { AttendanceFilters, type ClassOption } from "./_components/attendance-filters";
import { AttendanceMobileList } from "./_components/attendance-mobile-list";
import { AttendancePagination } from "./_components/attendance-pagination";
import {
  AttendanceEmpty,
  AttendanceError,
  AttendanceLoading,
} from "./_components/attendance-states";
import { AttendanceSummaryCards } from "./_components/attendance-summary";
import { AttendanceTable } from "./_components/attendance-table";

const PAGE_SIZE = 20;

export default function AttendancePage() {
  const authorities = useAuthorities();
  const canRead = hasPermission(authorities, "ATTENDANCE_READ");
  const isTeacher = hasRole(authorities, "TEACHER");
  const canMark = isTeacher && hasPermission(authorities, "ATTENDANCE_WRITE");

  if (!canRead) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Attendance</CardTitle>
          <CardDescription>You don&apos;t have permission to view attendance.</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return <AttendanceWorkspace isTeacher={isTeacher} canMark={canMark} />;
}

function AttendanceWorkspace({ isTeacher, canMark }: { isTeacher: boolean; canMark: boolean }) {
  const [date, setDate] = useState(todayIso);
  const [classValue, setClassValue] = useState("");
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<AttendanceStatusFilter>("all");
  const [page, setPage] = useState(0);
  const [detailStudentId, setDetailStudentId] = useState<number | null>(null);

  // ---- Class options (teacher: assigned classes only) ------------------------------------------
  const myTeacher = useMyTeacher(isTeacher);
  const assignments = useTeacherAssignments(myTeacher.data?.id);
  const yearsQuery = useAcademicYears();
  const currentYearId = yearsQuery.data?.find((y) => y.current)?.id;
  const classesQuery = useSchoolClasses({ page: 0, size: 100, academicYearId: currentYearId });

  const classOptions = useMemo<ClassOption[]>(() => {
    const classes = classesQuery.data?.content ?? [];
    const assigned = new Set((assignments.data ?? []).map((a) => a.schoolClassId));
    const homeroom = new Set(
      (assignments.data ?? []).filter((a) => a.homeroom).map((a) => a.schoolClassId),
    );
    return classes
      .filter((c) => !isTeacher || assigned.has(c.id))
      .map((c) => ({
        value: String(c.id),
        label: [
          c.name,
          c.gradeName,
          c.studyTrackName,
          homeroom.has(c.id) ? "Homeroom" : null,
        ]
          .filter(Boolean)
          .join(" · "),
      }));
  }, [classesQuery.data, assignments.data, isTeacher]);

  const selectedClassId =
    classValue && classOptions.some((o) => o.value === classValue)
      ? classValue
      : (classOptions[0]?.value ?? "");
  const selectedClass = classOptions.find((o) => o.value === selectedClassId);

  const classesLoading =
    yearsQuery.isLoading ||
    classesQuery.isLoading ||
    (isTeacher && (myTeacher.isLoading || assignments.isLoading));
  const classesError = isTeacher
    ? (myTeacher.error ?? assignments.error ?? classesQuery.error)
    : classesQuery.error;

  // ---- Roster + attendance ----------------------------------------------------------------
  const canQuery = Boolean(selectedClassId && date);
  const roster = useAttendanceRoster(
    selectedClassId ? Number(selectedClassId) : undefined,
    date,
  );
  const markMutation = useMarkAttendance();
  const bulkMutation = useMarkAttendanceBulk();

  const summary = useMemo(() => summarizeAttendance(roster.rows), [roster.rows]);

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    return roster.rows.filter((row) => {
      if (status === "UNMARKED") {
        if (row.status !== null) return false;
      } else if (status !== "all" && row.status !== status) {
        return false;
      }
      if (q) {
        const haystack = `${row.studentCode} ${row.studentName} ${row.englishName ?? ""}`.toLowerCase();
        if (!haystack.includes(q)) return false;
      }
      return true;
    });
  }, [roster.rows, search, status]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const safePage = Math.min(page, totalPages - 1);
  const pageRows = filtered.slice(safePage * PAGE_SIZE, safePage * PAGE_SIZE + PAGE_SIZE);
  const from = filtered.length === 0 ? 0 : safePage * PAGE_SIZE + 1;
  const to = safePage * PAGE_SIZE + pageRows.length;

  const detailRow = detailStudentId
    ? (roster.rows.find((r) => r.studentId === detailStudentId) ?? null)
    : null;

  const hasActiveFilters = search.trim() !== "" || status !== "all";
  const pendingStudentId = markMutation.isPending
    ? (markMutation.variables?.studentId ?? null)
    : null;
  const unmarkedRows = roster.rows.filter((r) => r.status === null);

  // ---- Handlers ---------------------------------------------------------------------------------
  const resetFilters = () => {
    setSearch("");
    setStatus("all");
    setPage(0);
  };

  const handleMark = (row: AttendanceRow, next: AttendanceStatus) => {
    if (!canMark || row.status === next) return;
    markMutation.mutate(
      {
        studentId: row.studentId,
        schoolClassId: row.schoolClassId,
        attendanceDate: row.date,
        status: next,
        remarks: row.remarks,
      },
      { onError: (error) => toast.error(toApiError(error).detail) },
    );
  };

  const handleMarkAllPresent = () => {
    if (!selectedClassId || !date || unmarkedRows.length === 0) return;
    bulkMutation.mutate(
      {
        schoolClassId: Number(selectedClassId),
        attendanceDate: date,
        entries: unmarkedRows.map((r) => ({ studentId: r.studentId, status: "PRESENT" })),
      },
      {
        onSuccess: (saved) =>
          toast.success(`Marked ${saved.length} student${saved.length === 1 ? "" : "s"} present.`),
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  };

  // ---- Content state ----------------------------------------------------------------------------
  let content: ReactNode;
  if (classesLoading) {
    content = <AttendanceLoading />;
  } else if (classesError) {
    content = <AttendanceError message={toApiError(classesError).detail} />;
  } else if (classOptions.length === 0) {
    content = (
      <AttendanceEmpty
        title="No classes available"
        description={
          isTeacher
            ? "You are not assigned to any class in the current academic year."
            : "No classes were found for the current academic year."
        }
      />
    );
  } else if (!date) {
    content = (
      <AttendanceEmpty title="Select a date" description="Choose a date to view attendance." />
    );
  } else if (roster.isLoading) {
    content = <AttendanceLoading />;
  } else if (roster.isError) {
    content = (
      <AttendanceError
        message={toApiError(roster.error).detail}
        onRetry={() => void roster.refetch()}
      />
    );
  } else if (roster.rows.length === 0) {
    content = (
      <AttendanceEmpty
        title="No students in this class"
        description="There are no active enrollments for the selected class."
      />
    );
  } else if (filtered.length === 0) {
    content = (
      <AttendanceEmpty
        title="No students found"
        description="No students match the current search or filters."
        action={
          <Button variant="outline" size="sm" onClick={resetFilters}>
            Clear filters
          </Button>
        }
      />
    );
  } else {
    content = (
      <>
        {summary.marked === 0 && (
          <div className="rounded-lg border border-dashed bg-muted/30 px-3 py-2 text-sm text-muted-foreground">
            No attendance has been recorded for this date yet.
            {canMark ? " Mark each student below or use “Mark all present”." : ""}
          </div>
        )}
        <AttendanceTable
          rows={pageRows}
          canMark={canMark}
          pendingStudentId={pendingStudentId}
          busy={bulkMutation.isPending}
          onMark={handleMark}
          onDetails={(row) => setDetailStudentId(row.studentId)}
        />
        <AttendanceMobileList
          rows={pageRows}
          canMark={canMark}
          pendingStudentId={pendingStudentId}
          busy={bulkMutation.isPending}
          onMark={handleMark}
          onDetails={(row) => setDetailStudentId(row.studentId)}
        />
        <AttendancePagination
          page={safePage}
          totalPages={totalPages}
          total={filtered.length}
          from={from}
          to={to}
          onPageChange={setPage}
        />
      </>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h1 className="text-xl font-semibold">Attendance</h1>
          <p className="text-sm text-muted-foreground">Manage and track student attendance</p>
          {selectedClass && date && (
            <p className="mt-1 text-sm text-muted-foreground">
              {selectedClass.label} · {formatLongDate(date)}
              {!canMark && " · Read-only"}
            </p>
          )}
        </div>
        <div className="flex flex-wrap gap-2">
          <Button
            variant="outline"
            onClick={() => void roster.refetch()}
            disabled={!canQuery || roster.isFetching}
          >
            <RefreshCwIcon className={cn(roster.isFetching && "animate-spin")} />
            Refresh
          </Button>
          {canMark && (
            <Button
              onClick={handleMarkAllPresent}
              disabled={!canQuery || unmarkedRows.length === 0 || bulkMutation.isPending}
            >
              {bulkMutation.isPending
                ? "Saving…"
                : `Mark all present${unmarkedRows.length > 0 ? ` (${unmarkedRows.length})` : ""}`}
            </Button>
          )}
        </div>
      </div>

      <AttendanceSummaryCards summary={summary} loading={!canQuery || roster.isLoading} />

      <Card>
        <CardContent className="space-y-4 pt-6">
          <AttendanceFilters
            date={date}
            maxDate={todayIso()}
            onDateChange={(value) => {
              setDate(value);
              setPage(0);
            }}
            classValue={selectedClassId}
            classOptions={classOptions}
            onClassChange={(value) => {
              setClassValue(value);
              setPage(0);
            }}
            search={search}
            onSearchChange={(value) => {
              setSearch(value);
              setPage(0);
            }}
            status={status}
            onStatusChange={(value) => {
              setStatus(value);
              setPage(0);
            }}
            hasActiveFilters={hasActiveFilters}
            onReset={resetFilters}
          />

          {content}
        </CardContent>
      </Card>

      <AttendanceDetailDialog
        row={detailRow}
        canEdit={canMark}
        onClose={() => setDetailStudentId(null)}
      />
    </div>
  );
}