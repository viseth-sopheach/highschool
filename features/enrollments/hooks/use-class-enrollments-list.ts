import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listClassEnrollments } from "@/features/enrollments/api/list-class-enrollments";
import type { EnrollmentListParams } from "@/features/enrollments/types";

export function useClassEnrollmentsList(
  schoolClassId: number | undefined,
  params: EnrollmentListParams,
) {
  return useQuery({
    queryKey: ["enrollments", "class", schoolClassId, "list", params],
    queryFn: () => listClassEnrollments(schoolClassId as number, params),
    enabled: typeof schoolClassId === "number" && Number.isFinite(schoolClassId),
    placeholderData: keepPreviousData,
  });
}