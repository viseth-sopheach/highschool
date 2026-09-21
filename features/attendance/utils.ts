import type { AttendanceRow } from "@/features/attendance/types";

export interface AttendanceSummary {
  total: number;
  present: number;
  absent: number;
  late: number;
  excused: number;
  unmarked: number;
  marked: number;
  /** Percentage of marked students who attended (present + late); null when nothing is marked. */
  rate: number | null;
}

export function summarizeAttendance(rows: AttendanceRow[]): AttendanceSummary {
  let present = 0;
  let absent = 0;
  let late = 0;
  let excused = 0;
  let unmarked = 0;

  for (const row of rows) {
    switch (row.status) {
      case "PRESENT":
        present++;
        break;
      case "ABSENT":
        absent++;
        break;
      case "LATE":
        late++;
        break;
      case "EXCUSED":
        excused++;
        break;
      default:
        unmarked++;
    }
  }

  const total = rows.length;
  const marked = total - unmarked;

  return {
    total,
    present,
    absent,
    late,
    excused,
    unmarked,
    marked,
    rate: marked > 0 ? Math.round(((present + late) / marked) * 100) : null,
  };
}

export function todayIso(): string {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
}

export function formatLongDate(iso: string): string {
  const [y, m, d] = iso.split("-").map(Number);
  if (!y || !m || !d) return iso;
  return new Date(y, m - 1, d).toLocaleDateString("en-GB", {
    weekday: "short",
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}