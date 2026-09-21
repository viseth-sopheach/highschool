// Mirrors com.seth.backend.attendance.dto.* exactly.
export const ATTENDANCE_STATUSES = ["PRESENT", "ABSENT", "LATE", "EXCUSED"] as const;

export type AttendanceStatus = (typeof ATTENDANCE_STATUSES)[number];

export type AttendanceStatusFilter = "all" | "UNMARKED" | AttendanceStatus;

export interface AttendanceResponse {
  id: number;
  studentId: number;
  studentName: string;
  schoolClassId: number;
  schoolClassName: string;
  attendanceDate: string; // ISO yyyy-MM-dd
  status: AttendanceStatus;
  remarks: string | null;
}

export interface AttendanceMarkRequest {
  studentId: number;
  schoolClassId: number;
  attendanceDate: string;
  status: AttendanceStatus;
  remarks?: string | null;
}

export interface AttendanceBulkEntry {
  studentId: number;
  status: AttendanceStatus;
  remarks?: string | null;
}

export interface AttendanceBulkMarkRequest {
  schoolClassId: number;
  attendanceDate: string;
  entries: AttendanceBulkEntry[];
}

/** UI model: one enrolled student merged with their attendance record (if any) for a date. */
export interface AttendanceRow {
  enrollmentId: number;
  studentId: number;
  studentCode: string;
  studentName: string;
  englishName: string | null;
  schoolClassId: number;
  schoolClassName: string;
  date: string;
  status: AttendanceStatus | null;
  remarks: string | null;
  recordId: number | null;
}