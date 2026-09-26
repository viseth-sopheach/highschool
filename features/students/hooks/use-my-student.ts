import { useQuery } from "@tanstack/react-query";
import { getMyStudent } from "@/features/students/api/get-my-student";

export function useMyStudent(enabled = true) {
  return useQuery({
    queryKey: ["students", "me"],
    queryFn: getMyStudent,
    enabled,
    staleTime: 10 * 60 * 1000,
    retry: false,
  });
}