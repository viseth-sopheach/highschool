import { useMutation, useQueryClient } from "@tanstack/react-query";
import { assignUserRoles } from "@/features/users/api/assign-user-roles";
import type { UserRoleAssignRequest } from "@/features/users/types";

export function useAssignUserRoles() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UserRoleAssignRequest }) =>
      assignUserRoles(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["users"] }),
  });
}