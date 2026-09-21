import { useQuery } from "@tanstack/react-query";
import { listClassAttendance } from "@/features/attendance/api/list-class-attendance";
import { attendanceKeys } from "@/features/attendance/cache";

export function useClassAttendance(schoolClassId: number | undefined, date: string) {
  return useQuery({
    queryKey: attendanceKeys.classOnDate(schoolClassId, date),
    queryFn: () => listClassAttendance(schoolClassId as number, date),
    enabled: typeof schoolClassId === "number" && Number.isFinite(schoolClassId) && Boolean(date),
  });
}