"use client";

import { useEffect, useState } from "react";
import { toast } from "sonner";
import { useEnrollStudent } from "@/features/enrollments/hooks/use-enroll-student";
import { useStudents } from "@/features/students/hooks/use-students";
import type { SchoolClassResponse } from "@/features/school-classes/types";
import { toApiError } from "@/lib/errors/api-error";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

interface EnrollmentFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  classes: SchoolClassResponse[];
  defaultClassId?: number;
}

export function EnrollmentFormDialog({
  open,
  onOpenChange,
  classes,
  defaultClassId,
}: EnrollmentFormDialogProps) {
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [studentId, setStudentId] = useState("");
  const [classId, setClassId] = useState(defaultClassId ? String(defaultClassId) : "");
  const [formError, setFormError] = useState<string | null>(null);

  const enrollMutation = useEnrollStudent();

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search.trim()), 300);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    if (open) {
      setSearch("");
      setDebouncedSearch("");
      setStudentId("");
      setClassId(defaultClassId ? String(defaultClassId) : "");
      setFormError(null);
    }
  }, [open, defaultClassId]);

  const studentsQuery = useStudents({ search: debouncedSearch || undefined, page: 0, size: 10 });
  const studentOptions = studentsQuery.data?.content ?? [];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!studentId) {
      setFormError("Select a student.");
      return;
    }
    if (!classId) {
      setFormError("Select a class.");
      return;
    }

    enrollMutation.mutate(
      { studentId: Number(studentId), schoolClassId: Number(classId) },
      {
        onSuccess: (enrollment) => {
          toast.success(`Enrolled student "${enrollment.studentCode}".`);
          onOpenChange(false);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Enroll student</DialogTitle>
          <DialogDescription>Search for a student and assign them to a class.</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="enroll-search">Search student</Label>
            <Input
              id="enroll-search"
              placeholder="Search by name or student code…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="space-y-2">
            <Label>Student</Label>
            <Select value={studentId} onValueChange={setStudentId}>
              <SelectTrigger className="w-full">
                <SelectValue
                  placeholder={
                    studentsQuery.isLoading
                      ? "Loading…"
                      : studentOptions.length === 0
                        ? "No students found"
                        : "Select a student"
                  }
                />
              </SelectTrigger>
              <SelectContent>
                {studentOptions.map((student) => (
                  <SelectItem key={student.id} value={String(student.id)}>
                    {student.studentCode} — {student.khmerName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>Class</Label>
            <Select value={classId} onValueChange={setClassId}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Select a class" />
              </SelectTrigger>
              <SelectContent>
                {classes.map((schoolClass) => (
                  <SelectItem key={schoolClass.id} value={String(schoolClass.id)}>
                    {schoolClass.name} · {schoolClass.gradeName}
                    {schoolClass.studyTrackName ? ` · ${schoolClass.studyTrackName}` : ""} ·{" "}
                    {schoolClass.academicYearName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {formError && <p className="text-sm text-destructive">{formError}</p>}

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={enrollMutation.isPending}>
              {enrollMutation.isPending ? "Enrolling…" : "Enroll"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}