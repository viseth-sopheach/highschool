import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateTeacher } from "@/features/teachers/api/update-teacher";
import type { TeacherUpdateRequest } from "@/features/teachers/types";

export function useUpdateTeacher() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: TeacherUpdateRequest }) =>
      updateTeacher(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["teachers"] }),
  });
}