import { httpClient } from "@/lib/api/http-client";
import type { PageResponse } from "@/lib/api/types";
import type { TeacherListParams, TeacherResponse } from "@/features/teachers/types";

export async function listTeachers(
  params: TeacherListParams,
): Promise<PageResponse<TeacherResponse>> {
  const { data } = await httpClient.get<PageResponse<TeacherResponse>>("/api/teachers", {
    params,
  });
  return data;
}