import { z } from "zod";
import { ATTENDANCE_STATUSES } from "@/features/attendance/types";

export const attendanceDetailSchema = z.object({
  status: z.enum(ATTENDANCE_STATUSES, { error: "Select a status" }),
  // Mirrors @Size(max = 255) remarks
  remarks: z
    .string()
    .trim()
    .max(255, "Remarks must be 255 characters or fewer")
    .optional()
    .transform((value) => (value ? value : undefined)),
});

export type AttendanceDetailFormInput = z.input<typeof attendanceDetailSchema>;
export type AttendanceDetailFormValues = z.output<typeof attendanceDetailSchema>;