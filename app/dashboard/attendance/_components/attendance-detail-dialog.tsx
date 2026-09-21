"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ATTENDANCE_STATUS_META } from "@/features/attendance/constants";
import {
  attendanceDetailSchema,
  type AttendanceDetailFormInput,
  type AttendanceDetailFormValues,
} from "@/features/attendance/schema";
import { useMarkAttendance } from "@/features/attendance/hooks/use-mark-attendance";
import {
  ATTENDANCE_STATUSES,
  type AttendanceRow,
  type AttendanceStatus,
} from "@/features/attendance/types";
import { formatLongDate } from "@/features/attendance/utils";
import { toApiError } from "@/lib/errors/api-error";
import { AttendanceStatusBadge } from "./attendance-status-badge";

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <div className="font-medium">{label}</div>
      <div className="text-muted-foreground">{children}</div>
    </div>
  );
}

interface AttendanceDetailDialogProps {
  row: AttendanceRow | null;
  canEdit: boolean;
  onClose: () => void;
}

export function AttendanceDetailDialog({ row, canEdit, onClose }: AttendanceDetailDialogProps) {
  return (
    <Dialog open={Boolean(row)} onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Attendance details</DialogTitle>
          <DialogDescription>
            {canEdit
              ? "Review or update this student's attendance for the selected date."
              : "Attendance record for the selected date."}
          </DialogDescription>
        </DialogHeader>
        {row && (
          <DetailBody
            key={`${row.studentId}:${row.date}:${row.status ?? "none"}`}
            row={row}
            canEdit={canEdit}
            onClose={onClose}
          />
        )}
      </DialogContent>
    </Dialog>
  );
}

function DetailBody({
  row,
  canEdit,
  onClose,
}: {
  row: AttendanceRow;
  canEdit: boolean;
  onClose: () => void;
}) {
  const markMutation = useMarkAttendance();
  const form = useForm<AttendanceDetailFormInput, unknown, AttendanceDetailFormValues>({
    resolver: zodResolver(attendanceDetailSchema),
    defaultValues: {
      status: row.status ?? "PRESENT",
      remarks: row.remarks ?? "",
    },
  });

  const errors = form.formState.errors;

  const onSubmit = form.handleSubmit((values) => {
    markMutation.mutate(
      {
        studentId: row.studentId,
        schoolClassId: row.schoolClassId,
        attendanceDate: row.date,
        status: values.status,
        remarks: values.remarks ?? null,
      },
      {
        onSuccess: () => {
          toast.success(`Attendance saved for ${row.studentCode}.`);
          onClose();
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  });

  const summary = (
    <div className="grid grid-cols-2 gap-3 text-sm">
      <Field label="Student ID">{row.studentCode}</Field>
      <Field label="Name">{row.studentName}</Field>
      <Field label="Class">{row.schoolClassName}</Field>
      <Field label="Date">{formatLongDate(row.date)}</Field>
    </div>
  );

  if (!canEdit) {
    return (
      <div className="space-y-4">
        {summary}
        <div className="grid grid-cols-2 gap-3 text-sm">
          <Field label="Status">
            <AttendanceStatusBadge status={row.status} />
          </Field>
          <Field label="Remarks">{row.remarks || "—"}</Field>
        </div>
        <DialogFooter>
          <Button type="button" variant="outline" onClick={onClose}>
            Close
          </Button>
        </DialogFooter>
      </div>
    );
  }

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      {summary}

      <div className="space-y-2">
        <Label htmlFor="detail-status">Status</Label>
        <Select
          value={form.watch("status")}
          onValueChange={(value) =>
            form.setValue("status", value as AttendanceStatus, { shouldValidate: true })
          }
        >
          <SelectTrigger id="detail-status" className="w-full">
            <SelectValue placeholder="Select a status" />
          </SelectTrigger>
          <SelectContent>
            {ATTENDANCE_STATUSES.map((status) => (
              <SelectItem key={status} value={status}>
                {ATTENDANCE_STATUS_META[status].label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        {errors.status && <p className="text-sm text-destructive">{errors.status.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="detail-remarks">Remarks</Label>
        <Input
          id="detail-remarks"
          maxLength={255}
          placeholder="Optional note (e.g. sick leave)"
          {...form.register("remarks")}
        />
        {errors.remarks && <p className="text-sm text-destructive">{errors.remarks.message}</p>}
      </div>

      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>
          Cancel
        </Button>
        <Button type="submit" disabled={markMutation.isPending}>
          {markMutation.isPending ? "Saving…" : "Save attendance"}
        </Button>
      </DialogFooter>
    </form>
  );
}