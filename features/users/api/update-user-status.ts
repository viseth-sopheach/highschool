import { httpClient } from "@/lib/api/http-client";
import type { UserResponse, UserStatusUpdateRequest } from "@/features/users/types";

export async function updateUserStatus(
  id: number,
  payload: UserStatusUpdateRequest,
): Promise<UserResponse> {
  const { data } = await httpClient.patch<UserResponse>(`/api/users/${id}/status`, payload);
  return data;
}