import { useQuery } from "@tanstack/react-query";
import { listGrades } from "@/features/grades/api/list-grades";

export function useGrades() {
  return useQuery({
    queryKey: ["grades"],
    queryFn: listGrades,
    staleTime: 5 * 60 * 1000,
  });
}