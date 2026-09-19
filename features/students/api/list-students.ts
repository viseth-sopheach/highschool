import { httpClient } from "@/lib/api/http-client";
import type { PageResponse } from "@/lib/api/types";
import type { StudentListParams, StudentResponse } from "@/features/students/types";

export async function listStudents(
  params: StudentListParams,
): Promise<PageResponse<StudentResponse>> {
  const { data } = await httpClient.get<PageResponse<StudentResponse>>("/api/students", {
    params,
  });
  return data;
}