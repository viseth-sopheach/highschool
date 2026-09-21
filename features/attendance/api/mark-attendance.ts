import { httpClient } from "@/lib/api/http-client";
import type { AttendanceMarkRequest, AttendanceResponse } from "@/features/attendance/types";

export async function markAttendance(payload: AttendanceMarkRequest): Promise<AttendanceResponse> {
  const { data } = await httpClient.post<AttendanceResponse>("/api/attendance", payload);
  return data;
}