import { httpClient } from "@/lib/api/http-client";
import type { TeacherResponse } from "@/features/teachers/types";

export async function getTeacher(id: number): Promise<TeacherResponse> {
  const { data } = await httpClient.get<TeacherResponse>(`/api/teachers/${id}`);
  return data;
}