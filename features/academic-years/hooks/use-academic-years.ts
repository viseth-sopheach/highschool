import { useQuery } from "@tanstack/react-query";
import { listAcademicYears } from "@/features/academic-years/api/list-academic-years";

export function useAcademicYears() {
  return useQuery({
    queryKey: ["academic-years"],
    queryFn: listAcademicYears,
    staleTime: 5 * 60 * 1000,
  });
}