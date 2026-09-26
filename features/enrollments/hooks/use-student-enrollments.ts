import { useQuery } from "@tanstack/react-query";
import { listStudentEnrollments } from "@/features/enrollments/api/list-student-enrollments";
import type { EnrollmentListParams } from "@/features/enrollments/types";

export function useStudentEnrollments(
  studentId: number | undefined,
  params: EnrollmentListParams,
) {
  return useQuery({
    queryKey: ["enrollments", "student", studentId, params],
    queryFn: () => listStudentEnrollments(studentId as number, params),
    enabled: typeof studentId === "number" && Number.isFinite(studentId),
  });
}