import { httpClient } from "@/lib/api/http-client";
import type { UserResponse } from "@/features/users/types";

export async function getUser(id: number): Promise<UserResponse> {
  const { data } = await httpClient.get<UserResponse>(`/api/users/${id}`);
  return data;
}