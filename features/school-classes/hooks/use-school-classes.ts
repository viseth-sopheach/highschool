import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listSchoolClasses } from "@/features/school-classes/api/list-school-classes";
import type { SchoolClassListParams } from "@/features/school-classes/types";

export function useSchoolClasses(params: SchoolClassListParams) {
  return useQuery({
    queryKey: ["school-classes", params],
    queryFn: () => listSchoolClasses(params),
    placeholderData: keepPreviousData,
  });
}