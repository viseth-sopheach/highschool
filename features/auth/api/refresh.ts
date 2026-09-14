import { httpClient } from "@/lib/api/http-client";
import type { AuthResponse } from "@/features/auth/types";

export async function refreshSession(refreshToken: string): Promise<AuthResponse> {
  const { data } = await httpClient.post<AuthResponse>("/api/auth/refresh", { refreshToken });
  return data;
}