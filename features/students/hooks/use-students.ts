import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listStudents } from "@/features/students/api/list-students";
import type { StudentListParams } from "@/features/students/types";

export function useStudents(params: StudentListParams) {
  return useQuery({
    queryKey: ["students", "list", params],
    queryFn: () => listStudents(params),
    placeholderData: keepPreviousData,
  });
}