import { httpClient } from "@/lib/api/http-client";
import type { StudentResponse, StudentUpdateRequest } from "@/features/students/types";

export async function updateStudent(
  id: number,
  payload: StudentUpdateRequest,
): Promise<StudentResponse> {
  const { data } = await httpClient.put<StudentResponse>(`/api/students/${id}`, payload);
  return data;
}