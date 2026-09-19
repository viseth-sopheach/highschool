import { httpClient } from "@/lib/api/http-client";
import type { TeacherCreateRequest, TeacherResponse } from "@/features/teachers/types";

export async function createTeacher(payload: TeacherCreateRequest): Promise<TeacherResponse> {
  const { data } = await httpClient.post<TeacherResponse>("/api/teachers", payload);
  return data;
}