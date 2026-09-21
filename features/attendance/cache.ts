import type { QueryClient } from "@tanstack/react-query";
import type { AttendanceResponse } from "@/features/attendance/types";

export const attendanceKeys = {
  classOnDate: (schoolClassId?: number, date?: string) =>
    ["attendance", "class", schoolClassId, date] as const,
};

/** Writes saved records straight into the cached class/date list so rows update without a refetch. */
export function upsertAttendanceCache(queryClient: QueryClient, saved: AttendanceResponse[]) {
  for (const record of saved) {
    queryClient.setQueryData<AttendanceResponse[]>(
      attendanceKeys.classOnDate(record.schoolClassId, record.attendanceDate),
      (old) => {
        if (!old) return old;
        const index = old.findIndex((r) => r.studentId === record.studentId);
        if (index === -1) return [...old, record];
        const next = old.slice();
        next[index] = record;
        return next;
      },
    );
  }
}