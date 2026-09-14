import { httpClient } from "@/lib/api/http-client";
import type { AuthResponse } from "@/features/auth/types";

export interface GoogleLoginRequest {
  idToken: string;
}

export async function googleLogin(payload: GoogleLoginRequest): Promise<AuthResponse> {
  const { data } = await httpClient.post<AuthResponse>("/api/auth/google", payload);
  return data;
}