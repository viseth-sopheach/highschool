import { Button } from "@/components/ui/button";

interface AttendancePaginationProps {
  page: number;
  totalPages: number;
  total: number;
  from: number;
  to: number;
  onPageChange: (page: number) => void;
}

export function AttendancePagination({
  page,
  totalPages,
  total,
  from,
  to,
  onPageChange,
}: AttendancePaginationProps) {
  return (
    <div className="flex flex-col gap-3 pt-2 sm:flex-row sm:items-center sm:justify-between">
      <p className="text-sm text-muted-foreground">
        Showing {from}–{to} of {total} student{total === 1 ? "" : "s"}
      </p>
      <div className="flex items-center justify-between gap-2 sm:justify-end">
        <Button
          variant="outline"
          size="sm"
          disabled={page === 0}
          onClick={() => onPageChange(Math.max(0, page - 1))}
        >
          Previous
        </Button>
        <span className="text-sm text-muted-foreground">
          Page {page + 1} of {totalPages}
        </span>
        <Button
          variant="outline"
          size="sm"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </Button>
      </div>
    </div>
  );
}