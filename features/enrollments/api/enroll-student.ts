import { httpClient } from "@/lib/api/http-client";
import type { StudentEnrollmentResponse } from "@/features/enrollments/types";

export interface EnrollStudentRequest {
  studentId: number;
  schoolClassId: number;
}

export async function enrollStudent(
  payload: EnrollStudentRequest,
): Promise<StudentEnrollmentResponse> {
  const { data } = await httpClient.post<StudentEnrollmentResponse>("/api/enrollments", payload);
  return data;
}