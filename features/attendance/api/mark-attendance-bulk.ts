import { httpClient } from "@/lib/api/http-client";
import type { AttendanceBulkMarkRequest, AttendanceResponse } from "@/features/attendance/types";

export async function markAttendanceBulk(
  payload: AttendanceBulkMarkRequest,
): Promise<AttendanceResponse[]> {
  const { data } = await httpClient.post<AttendanceResponse[]>("/api/attendance/bulk", payload);
  return data;
}