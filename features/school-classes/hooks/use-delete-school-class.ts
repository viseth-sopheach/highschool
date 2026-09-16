import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteSchoolClass } from "@/features/school-classes/api/delete-school-class";

export function useDeleteSchoolClass() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteSchoolClass,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["school-classes"] }),
  });
}