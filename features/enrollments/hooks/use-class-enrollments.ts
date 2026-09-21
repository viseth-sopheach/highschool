import { useQuery } from "@tanstack/react-query";
import { listClassEnrollments } from "@/features/enrollments/api/list-class-enrollments";

const ROSTER_PAGE_SIZE = 100;

export function useClassEnrollments(schoolClassId: number | undefined) {
  return useQuery({
    queryKey: ["enrollments", "class", schoolClassId],
    queryFn: () => listClassEnrollments(schoolClassId as number, { page: 0, size: ROSTER_PAGE_SIZE }),
    enabled: typeof schoolClassId === "number" && Number.isFinite(schoolClassId),
    staleTime: 2 * 60 * 1000,
  });
}