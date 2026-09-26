import { httpClient } from "@/lib/api/http-client";
import type { UserCreateRequest, UserResponse } from "@/features/users/types";

export async function createUser(payload: UserCreateRequest): Promise<UserResponse> {
  const { data } = await httpClient.post<UserResponse>("/api/users", payload);
  return data;
}