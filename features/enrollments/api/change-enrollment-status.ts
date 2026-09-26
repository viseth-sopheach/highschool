import { httpClient } from "@/lib/api/http-client";
import type { EnrollmentStatus, StudentEnrollmentResponse } from "@/features/enrollments/types";

export async function changeEnrollmentStatus(
  id: number,
  status: EnrollmentStatus,
): Promise<StudentEnrollmentResponse> {
  const { data } = await httpClient.patch<StudentEnrollmentResponse>(
    `/api/enrollments/${id}/status`,
    null,
    { params: { status } },
  );
  return data;
}