import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUserStatus } from "@/features/users/api/update-user-status";
import type { UserStatusUpdateRequest } from "@/features/users/types";

export function useUpdateUserStatus() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UserStatusUpdateRequest }) =>
      updateUserStatus(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["users"] }),
  });
}