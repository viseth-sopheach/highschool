"use client";

import { RotateCcwIcon, SearchIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
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
  ATTENDANCE_STATUSES,
  type AttendanceStatusFilter,
} from "@/features/attendance/types";

export interface ClassOption {
  value: string;
  label: string;
}

interface AttendanceFiltersProps {
  date: string;
  maxDate: string;
  onDateChange: (value: string) => void;
  classValue: string;
  classOptions: ClassOption[];
  onClassChange: (value: string) => void;
  search: string;
  onSearchChange: (value: string) => void;
  status: AttendanceStatusFilter;
  onStatusChange: (value: AttendanceStatusFilter) => void;
  hasActiveFilters: boolean;
  onReset: () => void;
}

export function AttendanceFilters({
  date,
  maxDate,
  onDateChange,
  classValue,
  classOptions,
  onClassChange,
  search,
  onSearchChange,
  status,
  onStatusChange,
  hasActiveFilters,
  onReset,
}: AttendanceFiltersProps) {
  return (
    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-[10rem_minmax(0,1fr)_minmax(0,1.2fr)_10rem_auto] xl:items-end">
      <div className="space-y-1.5">
        <Label htmlFor="attendance-date">Date</Label>
        <Input
          id="attendance-date"
          type="date"
          value={date}
          max={maxDate}
          onChange={(e) => onDateChange(e.target.value)}
        />
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="attendance-class">Class</Label>
        <Select value={classValue} onValueChange={onClassChange} disabled={classOptions.length === 0}>
          <SelectTrigger id="attendance-class" className="w-full">
            <SelectValue placeholder="Select a class" />
          </SelectTrigger>
          <SelectContent>
            {classOptions.map((option) => (
              <SelectItem key={option.value} value={option.value}>
                {option.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="attendance-search">Search student</Label>
        <div className="relative">
          <SearchIcon className="pointer-events-none absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            id="attendance-search"
            placeholder="Name or student ID…"
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-8"
          />
        </div>
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="attendance-status">Status</Label>
        <Select
          value={status}
          onValueChange={(value) => onStatusChange(value as AttendanceStatusFilter)}
        >
          <SelectTrigger id="attendance-status" className="w-full">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All statuses</SelectItem>
            {ATTENDANCE_STATUSES.map((s) => (
              <SelectItem key={s} value={s}>
                {ATTENDANCE_STATUS_META[s].label}
              </SelectItem>
            ))}
            <SelectItem value="UNMARKED">Not marked</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {hasActiveFilters && (
        <div className="sm:col-span-2 xl:col-span-1">
          <Button type="button" variant="ghost" onClick={onReset}>
            <RotateCcwIcon />
            Clear filters
          </Button>
        </div>
      )}
    </div>
  );
}