import { httpClient } from "@/lib/api/http-client";
import type { AttendanceResponse } from "@/features/attendance/types";

export async function listClassAttendance(
  schoolClassId: number,
  date: string,
): Promise<AttendanceResponse[]> {
  const { data } = await httpClient.get<AttendanceResponse[]>(
    `/api/attendance/by-class/${schoolClassId}`,
    { params: { date } },
  );
  return data;
}