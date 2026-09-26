import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listUsers } from "@/features/users/api/list-users";
import type { UserListParams } from "@/features/users/types";

export function useUsers(params: UserListParams) {
  return useQuery({
    queryKey: ["users", "list", params],
    queryFn: () => listUsers(params),
    placeholderData: keepPreviousData,
  });
}