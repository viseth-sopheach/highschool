import { cn } from "@/lib/utils";
import { ATTENDANCE_STATUS_META } from "@/features/attendance/constants";
import type { AttendanceStatus } from "@/features/attendance/types";

export function AttendanceStatusBadge({ status }: { status: AttendanceStatus | null }) {
  if (!status) {
    return (
      <span className="inline-flex items-center gap-1.5 rounded-full border border-dashed px-2 py-0.5 text-xs font-medium text-muted-foreground">
        <span className="size-1.5 rounded-full bg-muted-foreground/40" />
        Not marked
      </span>
    );
  }

  const meta = ATTENDANCE_STATUS_META[status];
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-xs font-medium",
        meta.badge,
      )}
    >
      <span className={cn("size-1.5 rounded-full", meta.dot)} />
      {meta.label}
    </span>
  );
}