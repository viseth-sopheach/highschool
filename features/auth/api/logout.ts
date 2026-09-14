import { httpClient } from "@/lib/api/http-client";

export async function logout(refreshToken: string): Promise<void> {
  await httpClient.post("/api/auth/logout", { refreshToken });
}