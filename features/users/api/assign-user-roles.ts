import { httpClient } from "@/lib/api/http-client";
import type { UserResponse, UserRoleAssignRequest } from "@/features/users/types";

export async function assignUserRoles(
  id: number,
  payload: UserRoleAssignRequest,
): Promise<UserResponse> {
  const { data } = await httpClient.put<UserResponse>(`/api/users/${id}/roles`, payload);
  return data;
}