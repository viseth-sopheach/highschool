import { useMutation, useQueryClient } from "@tanstack/react-query";
import { changeEnrollmentStatus } from "@/features/enrollments/api/change-enrollment-status";
import type { EnrollmentStatus } from "@/features/enrollments/types";

export function useChangeEnrollmentStatus() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: EnrollmentStatus }) =>
      changeEnrollmentStatus(id, status),
    onSuccess: (enrollment) => {
      queryClient.invalidateQueries({ queryKey: ["enrollments", "class", enrollment.schoolClassId] });
      queryClient.invalidateQueries({ queryKey: ["enrollments", "student", enrollment.studentId] });
    },
  });
}