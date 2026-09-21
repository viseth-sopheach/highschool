"use client";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { ATTENDANCE_STATUS_META } from "@/features/attendance/constants";
import {
  ATTENDANCE_STATUSES,
  type AttendanceRow,
  type AttendanceStatus,
} from "@/features/attendance/types";

interface AttendanceStatusActionsProps {
  row: AttendanceRow;
  disabled?: boolean;
  onMark: (row: AttendanceRow, status: AttendanceStatus) => void;
  className?: string;
}

export function AttendanceStatusActions({
  row,
  disabled,
  onMark,
  className,
}: AttendanceStatusActionsProps) {
  return (
    <div
      role="group"
      aria-label={`Mark attendance for ${row.studentName}`}
      className={cn("flex flex-wrap gap-1.5", className)}
    >
      {ATTENDANCE_STATUSES.map((status) => {
        const active = row.status === status;
        return (
          <Button
            key={status}
            type="button"
            size="sm"
            variant="outline"
            disabled={disabled}
            aria-pressed={active}
            onClick={() => onMark(row, status)}
            className={cn(active && ATTENDANCE_STATUS_META[status].active)}
          >
            {ATTENDANCE_STATUS_META[status].label}
          </Button>
        );
      })}
    </div>
  );
}