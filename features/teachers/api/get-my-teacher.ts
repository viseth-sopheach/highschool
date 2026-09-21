import { httpClient } from "@/lib/api/http-client";
import type { TeacherResponse } from "@/features/teachers/types";

export async function getMyTeacher(): Promise<TeacherResponse> {
  const { data } = await httpClient.get<TeacherResponse>("/api/teachers/me");
  return data;
}