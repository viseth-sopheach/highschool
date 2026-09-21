import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import type { AttendanceSummary } from "@/features/attendance/utils";

interface StatCardProps {
  label: string;
  value: string;
  hint?: string;
  tone?: string;
  className?: string;
}

function StatCard({ label, value, hint, tone, className }: StatCardProps) {
  return (
    <Card size="sm" className={className}>
      <CardContent className="space-y-1">
        <p className="text-xs font-medium text-muted-foreground">{label}</p>
        <p className={cn("text-2xl font-semibold tabular-nums", tone)}>{value}</p>
        {hint && <p className="text-xs text-muted-foreground">{hint}</p>}
      </CardContent>
    </Card>
  );
}

export function AttendanceSummaryCards({
  summary,
  loading,
}: {
  summary: AttendanceSummary;
  loading: boolean;
}) {
  const show = (n: number) => (loading ? "—" : String(n));

  return (
    <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
      <StatCard
        className="col-span-2 sm:col-span-1"
        label="Total Students"
        value={show(summary.total)}
        hint={
          loading
            ? undefined
            : `${summary.unmarked} unmarked · ${summary.excused} excused`
        }
      />
      <StatCard
        label="Present"
        value={show(summary.present)}
        tone="text-emerald-600 dark:text-emerald-400"
      />
      <StatCard
        label="Absent"
        value={show(summary.absent)}
        tone="text-red-600 dark:text-red-400"
      />
      <StatCard
        label="Late"
        value={show(summary.late)}
        tone="text-amber-600 dark:text-amber-400"
      />
      <StatCard
        label="Attendance Rate"
        value={loading || summary.rate === null ? "—" : `${summary.rate}%`}
        hint={loading ? undefined : `of ${summary.marked} marked`}
      />
    </div>
  );
}