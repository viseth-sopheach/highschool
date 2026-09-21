import { useQuery } from "@tanstack/react-query";
import { listTeacherAssignments } from "@/features/class-teacher-assignments/api/list-teacher-assignments";

export function useTeacherAssignments(teacherId: number | undefined) {
  return useQuery({
    queryKey: ["class-teacher-assignments", "teacher", teacherId],
    queryFn: () => listTeacherAssignments(teacherId as number),
    enabled: typeof teacherId === "number" && Number.isFinite(teacherId),
    staleTime: 5 * 60 * 1000,
  });
}