import { httpClient } from "@/lib/api/http-client";
import type { AuthResponse, LoginRequest } from "@/features/auth/types";

export async function login(payload: LoginRequest): Promise<AuthResponse> {
  const { data } = await httpClient.post<AuthResponse>("/api/auth/login", payload);
  return data;
}