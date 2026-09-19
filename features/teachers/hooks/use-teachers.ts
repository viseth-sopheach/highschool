import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listTeachers } from "@/features/teachers/api/list-teachers";
import type { TeacherListParams } from "@/features/teachers/types";

export function useTeachers(params: TeacherListParams) {
  return useQuery({
    queryKey: ["teachers", "list", params],
    queryFn: () => listTeachers(params),
    placeholderData: keepPreviousData,
  });
}