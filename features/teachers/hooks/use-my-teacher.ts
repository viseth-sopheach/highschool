import { useQuery } from "@tanstack/react-query";
import { getMyTeacher } from "@/features/teachers/api/get-my-teacher";

export function useMyTeacher(enabled = true) {
  return useQuery({
    queryKey: ["teachers", "me"],
    queryFn: getMyTeacher,
    enabled,
    staleTime: 10 * 60 * 1000,
    retry: false,
  });
}