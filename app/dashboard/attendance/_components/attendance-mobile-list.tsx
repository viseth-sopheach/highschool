"use client";

import { Button } from "@/components/ui/button";
import type { AttendanceRow, AttendanceStatus } from "@/features/attendance/types";
import { AttendanceStatusActions } from "./attendance-status-actions";
import { AttendanceStatusBadge } from "./attendance-status-badge";

interface AttendanceMobileListProps {
  rows: AttendanceRow[];
  canMark: boolean;
  pendingStudentId: number | null;
  busy: boolean;
  onMark: (row: AttendanceRow, status: AttendanceStatus) => void;
  onDetails: (row: AttendanceRow) => void;
}

export function AttendanceMobileList({
  rows,
  canMark,
  pendingStudentId,
  busy,
  onMark,
  onDetails,
}: AttendanceMobileListProps) {
  return (
    <ul className="space-y-2 md:hidden">
      {rows.map((row) => (
        <li key={row.studentId} className="space-y-3 rounded-lg border p-3">
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">
              <p className="truncate text-sm font-medium">{row.studentName}</p>
              <p className="text-xs text-muted-foreground">
                {row.studentCode}
                {row.schoolClassName ? ` · ${row.schoolClassName}` : ""}
              </p>
            </div>
            <AttendanceStatusBadge status={row.status} />
          </div>

          {canMark && (
            <AttendanceStatusActions
              row={row}
              disabled={busy || pendingStudentId === row.studentId}
              onMark={onMark}
              className="grid grid-cols-2 sm:grid-cols-4"
            />
          )}

          <Button
            type="button"
            variant="ghost"
            size="sm"
            className="w-full"
            onClick={() => onDetails(row)}
          >
            {canMark ? "Edit details" : "View details"}
          </Button>
        </li>
      ))}
    </ul>
  );
}