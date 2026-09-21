import { useMemo } from "react";
import { useQueries } from "@tanstack/react-query";
import { getStudent } from "@/features/students/api/get-student";
import type { StudentResponse } from "@/features/students/types";
import { useClassEnrollments } from "@/features/enrollments/hooks/use-class-enrollments";
import { useClassAttendance } from "@/features/attendance/hooks/use-class-attendance";
import type { AttendanceRow } from "@/features/attendance/types";

/** Merges the class roster (active enrollments), student names, and the attendance records for one date. */
export function useAttendanceRoster(schoolClassId: number | undefined, date: string) {
  const enrollmentsQuery = useClassEnrollments(schoolClassId);
  const attendanceQuery = useClassAttendance(schoolClassId, date);

  const activeEnrollments = useMemo(
    () =>
      (enrollmentsQuery.data?.content ?? [])
        .filter((e) => e.status === "ACTIVE")
        .sort((a, b) => a.studentCode.localeCompare(b.studentCode, undefined, { numeric: true })),
    [enrollmentsQuery.data],
  );

  const students = useQueries({
    queries: activeEnrollments.map((enrollment) => ({
      queryKey: ["students", "detail", enrollment.studentId],
      queryFn: () => getStudent(enrollment.studentId),
      staleTime: 10 * 60 * 1000,
    })),
    combine: (results) => ({
      byId: Object.fromEntries(
        results.flatMap((r) => (r.data ? [[r.data.id, r.data] as const] : [])),
      ) as Record<number, StudentResponse>,
    }),
  });

  const rows = useMemo<AttendanceRow[]>(() => {
    const records = new Map((attendanceQuery.data ?? []).map((r) => [r.studentId, r]));
    return activeEnrollments.map((enrollment) => {
      const record = records.get(enrollment.studentId);
      const student = students.byId[enrollment.studentId];
      return {
        enrollmentId: enrollment.id,
        studentId: enrollment.studentId,
        studentCode: enrollment.studentCode,
        studentName: student?.khmerName ?? record?.studentName ?? enrollment.studentCode,
        englishName: student?.englishName ?? null,
        schoolClassId: enrollment.schoolClassId,
        schoolClassName: enrollment.schoolClassName,
        date,
        status: record?.status ?? null,
        remarks: record?.remarks ?? null,
        recordId: record?.id ?? null,
      };
    });
  }, [activeEnrollments, attendanceQuery.data, students.byId, date]);

  return {
    rows,
    isLoading: enrollmentsQuery.isLoading || attendanceQuery.isLoading,
    isFetching: enrollmentsQuery.isFetching || attendanceQuery.isFetching,
    isError: enrollmentsQuery.isError || attendanceQuery.isError,
    error: enrollmentsQuery.error ?? attendanceQuery.error,
    refetch: () => Promise.all([enrollmentsQuery.refetch(), attendanceQuery.refetch()]),
  };
}