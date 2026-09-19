import { httpClient } from "@/lib/api/http-client";
import type { StudentCreateRequest, StudentResponse } from "@/features/students/types";

export async function createStudent(payload: StudentCreateRequest): Promise<StudentResponse> {
  const { data } = await httpClient.post<StudentResponse>("/api/students", payload);
  return data;
}