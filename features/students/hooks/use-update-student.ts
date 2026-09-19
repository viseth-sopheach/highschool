import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateStudent } from "@/features/students/api/update-student";
import type { StudentUpdateRequest } from "@/features/students/types";

export function useUpdateStudent() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: StudentUpdateRequest }) =>
      updateStudent(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["students"] }),
  });
}