import { useMutation, useQueryClient } from "@tanstack/react-query";
import { markAttendanceBulk } from "@/features/attendance/api/mark-attendance-bulk";
import { upsertAttendanceCache } from "@/features/attendance/cache";

export function useMarkAttendanceBulk() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: markAttendanceBulk,
    onSuccess: (saved) => upsertAttendanceCache(queryClient, saved),
  });
}