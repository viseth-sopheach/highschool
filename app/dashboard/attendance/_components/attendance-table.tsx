"use client";

import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { formatLongDate } from "@/features/attendance/utils";
import type { AttendanceRow, AttendanceStatus } from "@/features/attendance/types";
import { AttendanceStatusActions } from "./attendance-status-actions";
import { AttendanceStatusBadge } from "./attendance-status-badge";

interface AttendanceTableProps {
  rows: AttendanceRow[];
  canMark: boolean;
  pendingStudentId: number | null;
  busy: boolean;
  onMark: (row: AttendanceRow, status: AttendanceStatus) => void;
  onDetails: (row: AttendanceRow) => void;
}

export function AttendanceTable({
  rows,
  canMark,
  pendingStudentId,
  busy,
  onMark,
  onDetails,
}: AttendanceTableProps) {
  return (
    <div className="hidden md:block">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Student ID</TableHead>
            <TableHead>Student name</TableHead>
            <TableHead className="hidden lg:table-cell">Class</TableHead>
            <TableHead className="hidden lg:table-cell">Date</TableHead>
            <TableHead>Status</TableHead>
            <TableHead className="text-right">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.studentId}>
              <TableCell className="font-medium">{row.studentCode}</TableCell>
              <TableCell>
                <div>{row.studentName}</div>
                {row.englishName && (
                  <div className="text-xs text-muted-foreground">{row.englishName}</div>
                )}
              </TableCell>
              <TableCell className="hidden lg:table-cell">{row.schoolClassName}</TableCell>
              <TableCell className="hidden lg:table-cell">{formatLongDate(row.date)}</TableCell>
              <TableCell>
                <AttendanceStatusBadge status={row.status} />
              </TableCell>
              <TableCell>
                <div className="flex items-center justify-end gap-1.5">
                  {canMark && (
                    <AttendanceStatusActions
                      row={row}
                      disabled={busy || pendingStudentId === row.studentId}
                      onMark={onMark}
                      className="flex-nowrap"
                    />
                  )}
                  <Button type="button" variant="ghost" size="sm" onClick={() => onDetails(row)}>
                    {canMark ? "Edit" : "View"}
                  </Button>
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}