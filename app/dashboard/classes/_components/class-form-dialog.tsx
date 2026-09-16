"use client";

import { useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import {
  schoolClassCreateSchema,
  schoolClassUpdateSchema,
  type SchoolClassCreateFormInput,
  type SchoolClassCreateFormValues,
  type SchoolClassUpdateFormInput,
  type SchoolClassUpdateFormValues,
} from "@/features/school-classes/schema";
import { useCreateSchoolClass } from "@/features/school-classes/hooks/use-create-school-class";
import { useUpdateSchoolClass } from "@/features/school-classes/hooks/use-update-school-class";
import type { SchoolClassResponse } from "@/features/school-classes/types";
import type { AcademicYearResponse } from "@/features/academic-years/types";
import type { GradeResponse } from "@/features/grades/types";
import type { StudyTrackResponse } from "@/features/study-tracks/types";
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

interface ClassFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  schoolClass?: SchoolClassResponse;
  academicYears: AcademicYearResponse[];
  grades: GradeResponse[];
  studyTracks: StudyTrackResponse[];
}

export function ClassFormDialog({
  open,
  onOpenChange,
  schoolClass,
  academicYears,
  grades,
  studyTracks,
}: ClassFormDialogProps) {
  if (schoolClass) {
    return (
      <EditClassDialog
        key={schoolClass.id}
        open={open}
        onOpenChange={onOpenChange}
        schoolClass={schoolClass}
        grades={grades}
        studyTracks={studyTracks}
      />
    );
  }
  return (
    <CreateClassDialog
      open={open}
      onOpenChange={onOpenChange}
      academicYears={academicYears}
      grades={grades}
      studyTracks={studyTracks}
    />
  );
}

function CreateClassDialog({
  open,
  onOpenChange,
  academicYears,
  grades,
  studyTracks,
}: Omit<ClassFormDialogProps, "schoolClass">) {
  const createMutation = useCreateSchoolClass();
  const form = useForm<SchoolClassCreateFormInput, unknown, SchoolClassCreateFormValues>({
    resolver: zodResolver(schoolClassCreateSchema),
    defaultValues: { academicYearId: "", gradeId: "", studyTrackId: "", name: "", capacity: "" },
  });

  const gradeId = form.watch("gradeId");
  const selectedGrade = useMemo(() => grades.find((g) => String(g.id) === gradeId), [grades, gradeId]);
  const requiresTrack = selectedGrade?.requiresTrack;

  useEffect(() => {
    if (!open) form.reset();
  }, [open, form]);

  const onSubmit = form.handleSubmit((values) => {
    if (requiresTrack && !values.studyTrackId) {
      form.setError("studyTrackId", { message: "This grade requires a study track" });
      return;
    }
    if (requiresTrack === false && values.studyTrackId) {
      form.setError("studyTrackId", { message: "This grade must not have a study track" });
      return;
    }
    createMutation.mutate(
      {
        academicYearId: values.academicYearId,
        gradeId: values.gradeId,
        studyTrackId: values.studyTrackId ?? null,
        name: values.name,
        capacity: values.capacity ?? null,
      },
      {
        onSuccess: () => {
          toast.success(`Class "${values.name}" created.`);
          onOpenChange(false);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New class</DialogTitle>
          <DialogDescription>Create a class for a grade within an academic year.</DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label>Academic year</Label>
              <Select
                value={form.watch("academicYearId")}
                onValueChange={(v) => form.setValue("academicYearId", v, { shouldValidate: true })}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a year" />
                </SelectTrigger>
                <SelectContent>
                  {academicYears.map((year) => (
                    <SelectItem key={year.id} value={String(year.id)}>
                      {year.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {form.formState.errors.academicYearId && (
                <p className="text-sm text-destructive">{form.formState.errors.academicYearId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label>Grade</Label>
              <Select
                value={gradeId}
                onValueChange={(v) => {
                  form.setValue("gradeId", v, { shouldValidate: true });
                  form.setValue("studyTrackId", "", { shouldValidate: true });
                }}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a grade" />
                </SelectTrigger>
                <SelectContent>
                  {grades.map((grade) => (
                    <SelectItem key={grade.id} value={String(grade.id)}>
                      {grade.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {form.formState.errors.gradeId && (
                <p className="text-sm text-destructive">{form.formState.errors.gradeId.message}</p>
              )}
            </div>
          </div>

          {requiresTrack && (
            <div className="space-y-2">
              <Label>Study track *</Label>
              <Select
                value={form.watch("studyTrackId")}
                onValueChange={(v) => form.setValue("studyTrackId", v, { shouldValidate: true })}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a track" />
                </SelectTrigger>
                <SelectContent>
                  {studyTracks.map((track) => (
                    <SelectItem key={track.id} value={String(track.id)}>
                      {track.nameEn}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {form.formState.errors.studyTrackId && (
                <p className="text-sm text-destructive">{form.formState.errors.studyTrackId.message}</p>
              )}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="create-name">Class name</Label>
            <Input id="create-name" placeholder="e.g. 11A" maxLength={10} {...form.register("name")} />
            {form.formState.errors.name && (
              <p className="text-sm text-destructive">{form.formState.errors.name.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-capacity">Capacity</Label>
            <Input id="create-capacity" type="number" min={1} placeholder="Optional" {...form.register("capacity")} />
            {form.formState.errors.capacity && (
              <p className="text-sm text-destructive">{form.formState.errors.capacity.message as string}</p>
            )}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? "Creating…" : "Create class"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function EditClassDialog({
  open,
  onOpenChange,
  schoolClass,
  grades,
  studyTracks,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  schoolClass: SchoolClassResponse;
  grades: GradeResponse[];
  studyTracks: StudyTrackResponse[];
}) {
  const updateMutation = useUpdateSchoolClass();
  const grade = grades.find((g) => g.id === schoolClass.gradeId);
  const requiresTrack = grade?.requiresTrack;

  const form = useForm<SchoolClassUpdateFormInput, unknown, SchoolClassUpdateFormValues>({
    resolver: zodResolver(schoolClassUpdateSchema),
    defaultValues: {
      name: schoolClass.name,
      studyTrackId: schoolClass.studyTrackId ? String(schoolClass.studyTrackId) : "",
      capacity: schoolClass.capacity ? String(schoolClass.capacity) : "",
    },
  });

  const onSubmit = form.handleSubmit((values) => {
    if (requiresTrack && !values.studyTrackId) {
      form.setError("studyTrackId", { message: "This grade requires a study track" });
      return;
    }
    if (requiresTrack === false && values.studyTrackId) {
      form.setError("studyTrackId", { message: "This grade must not have a study track" });
      return;
    }
    updateMutation.mutate(
      {
        id: schoolClass.id,
        payload: {
          name: values.name,
          studyTrackId: values.studyTrackId ?? null,
          capacity: values.capacity ?? null,
        },
      },
      {
        onSuccess: () => {
          toast.success(`Class "${values.name}" updated.`);
          onOpenChange(false);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit class</DialogTitle>
          <DialogDescription>Update this class&apos;s name, study track, or capacity.</DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <div className="font-medium">Academic year</div>
              <div className="text-muted-foreground">{schoolClass.academicYearName}</div>
            </div>
            <div>
              <div className="font-medium">Grade</div>
              <div className="text-muted-foreground">{schoolClass.gradeName}</div>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-name">Class name</Label>
            <Input id="edit-name" maxLength={10} {...form.register("name")} />
            {form.formState.errors.name && (
              <p className="text-sm text-destructive">{form.formState.errors.name.message}</p>
            )}
          </div>

          {requiresTrack !== false && (
            <div className="space-y-2">
              <Label>Study track{requiresTrack ? " *" : ""}</Label>
              <Select
                value={form.watch("studyTrackId")}
                onValueChange={(v) => form.setValue("studyTrackId", v, { shouldValidate: true })}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a track" />
                </SelectTrigger>
                <SelectContent>
                  {studyTracks.map((track) => (
                    <SelectItem key={track.id} value={String(track.id)}>
                      {track.nameEn}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {form.formState.errors.studyTrackId && (
                <p className="text-sm text-destructive">{form.formState.errors.studyTrackId.message}</p>
              )}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="edit-capacity">Capacity</Label>
            <Input id="edit-capacity" type="number" min={1} {...form.register("capacity")} />
            {form.formState.errors.capacity && (
              <p className="text-sm text-destructive">{form.formState.errors.capacity.message as string}</p>
            )}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={updateMutation.isPending}>
              {updateMutation.isPending ? "Saving…" : "Save changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}