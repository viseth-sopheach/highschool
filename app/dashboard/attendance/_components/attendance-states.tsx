import type { ReactNode } from "react";
import { Button } from "@/components/ui/button";

export function AttendanceLoading() {
  return (
    <div className="space-y-2" role="status" aria-live="polite">
      {Array.from({ length: 8 }).map((_, index) => (
        <div key={index} className="h-11 animate-pulse rounded-lg bg-muted/60" />
      ))}
      <span className="sr-only">Loading attendance…</span>
    </div>
  );
}

export function AttendanceEmpty({
  title,
  description,
  action,
}: {
  title: string;
  description: string;
  action?: ReactNode;
}) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed px-4 py-12 text-center">
      <p className="text-sm font-medium">{title}</p>
      <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      {action}
    </div>
  );
}

export function AttendanceError({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <div
      role="alert"
      className="flex flex-col items-center justify-center gap-2 rounded-lg border border-destructive/30 bg-destructive/5 px-4 py-12 text-center"
    >
      <p className="text-sm font-medium text-destructive">Something went wrong</p>
      <p className="max-w-sm text-sm text-muted-foreground">{message}</p>
      {onRetry && (
        <Button variant="outline" size="sm" onClick={onRetry}>
          Try again
        </Button>
      )}
    </div>
  );
}