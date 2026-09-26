import { httpClient } from "@/lib/api/http-client";
import type { StudentResponse } from "@/features/students/types";

export async function getMyStudent(): Promise<StudentResponse> {
  const { data } = await httpClient.get<StudentResponse>("/api/students/me");
  return data;
}