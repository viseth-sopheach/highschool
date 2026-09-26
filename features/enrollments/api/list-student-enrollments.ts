import { httpClient } from "@/lib/api/http-client";
import type { PageResponse } from "@/lib/api/types";
import type { EnrollmentListParams, StudentEnrollmentResponse } from "@/features/enrollments/types";

export async function listStudentEnrollments(
  studentId: number,
  params: EnrollmentListParams,
): Promise<PageResponse<StudentEnrollmentResponse>> {
  const { data } = await httpClient.get<PageResponse<StudentEnrollmentResponse>>(
    `/api/enrollments/by-student/${studentId}`,
    { params },
  );
  return data;
}