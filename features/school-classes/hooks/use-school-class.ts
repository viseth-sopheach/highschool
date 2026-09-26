import { useQuery } from "@tanstack/react-query";
import { getSchoolClass } from "@/features/school-classes/api/get-school-class";

export function useSchoolClass(id: number, enabled = true) {
  return useQuery({
    queryKey: ["school-classes", "detail", id],
    queryFn: () => getSchoolClass(id),
    enabled: enabled && Number.isFinite(id),
  });
}