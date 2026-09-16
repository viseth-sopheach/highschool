import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateSchoolClass } from "@/features/school-classes/api/update-school-class";
import type { SchoolClassUpdateRequest } from "@/features/school-classes/types";

export function useUpdateSchoolClass() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: SchoolClassUpdateRequest }) =>
      updateSchoolClass(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["school-classes"] }),
  });
}