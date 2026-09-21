import { useMutation, useQueryClient } from "@tanstack/react-query";
import { markAttendance } from "@/features/attendance/api/mark-attendance";
import { upsertAttendanceCache } from "@/features/attendance/cache";

export function useMarkAttendance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: markAttendance,
    onSuccess: (saved) => upsertAttendanceCache(queryClient, [saved]),
  });
}