import { useQuery } from "@tanstack/react-query";
import { getTeacher } from "@/features/teachers/api/get-teacher";

export function useTeacher(id: number, enabled = true) {
  return useQuery({
    queryKey: ["teachers", "detail", id],
    queryFn: () => getTeacher(id),
    enabled: enabled && Number.isFinite(id),
  });
}