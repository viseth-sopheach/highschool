import { httpClient } from "@/lib/api/http-client";
import type { TeacherResponse, TeacherUpdateRequest } from "@/features/teachers/types";

export async function updateTeacher(
  id: number,
  payload: TeacherUpdateRequest,
): Promise<TeacherResponse> {
  const { data } = await httpClient.put<TeacherResponse>(`/api/teachers/${id}`, payload);
  return data;
}