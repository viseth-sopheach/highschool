import { useQuery } from "@tanstack/react-query";
import { getUser } from "@/features/users/api/get-user";

export function useUser(id: number, enabled = true) {
  return useQuery({
    queryKey: ["users", "detail", id],
    queryFn: () => getUser(id),
    enabled: enabled && Number.isFinite(id),
  });
}