import { useQuery } from "@tanstack/react-query";
import { getStudent } from "@/features/students/api/get-student";

export function useStudent(id: number, enabled = true) {
  return useQuery({
    queryKey: ["students", "detail", id],
    queryFn: () => getStudent(id),
    enabled: enabled && Number.isFinite(id),
  });
}