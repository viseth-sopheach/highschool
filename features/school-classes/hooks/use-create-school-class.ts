import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createSchoolClass } from "@/features/school-classes/api/create-school-class";

export function useCreateSchoolClass() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createSchoolClass,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["school-classes"] }),
  });
}