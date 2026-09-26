import { httpClient } from "@/lib/api/http-client";
import type { PageResponse } from "@/lib/api/types";
import type { UserListParams, UserResponse } from "@/features/users/types";

export async function listUsers(params: UserListParams): Promise<PageResponse<UserResponse>> {
  const { data } = await httpClient.get<PageResponse<UserResponse>>("/api/users", { params });
  return data;
}