import type { AttendanceStatus } from "@/features/attendance/types";

export interface AttendanceStatusMeta {
  label: string;
  badge: string;
  dot: string;
  active: string;
}

export const ATTENDANCE_STATUS_META: Record<AttendanceStatus, AttendanceStatusMeta> = {
  PRESENT: {
    label: "Present",
    badge:
      "border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-300",
    dot: "bg-emerald-500",
    active:
      "border-emerald-600 bg-emerald-600 text-white hover:bg-emerald-600/90 hover:text-white dark:border-emerald-500 dark:bg-emerald-600",
  },
  ABSENT: {
    label: "Absent",
    badge:
      "border-red-200 bg-red-50 text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-300",
    dot: "bg-red-500",
    active:
      "border-red-600 bg-red-600 text-white hover:bg-red-600/90 hover:text-white dark:border-red-500 dark:bg-red-600",
  },
  LATE: {
    label: "Late",
    badge:
      "border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300",
    dot: "bg-amber-500",
    active:
      "border-amber-500 bg-amber-500 text-white hover:bg-amber-500/90 hover:text-white dark:border-amber-500 dark:bg-amber-600",
  },
  EXCUSED: {
    label: "Excused",
    badge:
      "border-sky-200 bg-sky-50 text-sky-700 dark:border-sky-900 dark:bg-sky-950/40 dark:text-sky-300",
    dot: "bg-sky-500",
    active:
      "border-sky-600 bg-sky-600 text-white hover:bg-sky-600/90 hover:text-white dark:border-sky-500 dark:bg-sky-600",
  },
};