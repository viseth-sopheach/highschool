import { useMutation, useQueryClient } from "@tanstack/react-query";
import { enrollStudent } from "@/features/enrollments/api/enroll-student";

export function useEnrollStudent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: enrollStudent,
    onSuccess: (enrollment) => {
      queryClient.invalidateQueries({ queryKey: ["enrollments", "class", enrollment.schoolClassId] });
      queryClient.invalidateQueries({ queryKey: ["enrollments", "student", enrollment.studentId] });
    },
  });
}