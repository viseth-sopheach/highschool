import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteTeacher } from "@/features/teachers/api/delete-teacher";

export function useDeleteTeacher() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteTeacher,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["teachers"] }),
  });
}