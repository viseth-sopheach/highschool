"use client";

import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import {
  studentCreateSchema,
  studentUpdateSchema,
  type StudentCreateFormInput,
  type StudentCreateFormValues,
  type StudentUpdateFormInput,
  type StudentUpdateFormValues,
} from "@/features/students/schema";
import { useCreateStudent } from "@/features/students/hooks/use-create-student";
import { useUpdateStudent } from "@/features/students/hooks/use-update-student";
import type { StudentResponse } from "@/features/students/types";
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

interface StudentFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  student?: StudentResponse;
}

const GENDER_OPTIONS = [
  { value: "MALE", label: "Male" },
  { value: "FEMALE", label: "Female" },
];

export function StudentFormDialog({ open, onOpenChange, student }: StudentFormDialogProps) {
  if (student) {
    return <EditStudentDialog key={student.id} open={open} onOpenChange={onOpenChange} student={student} />;
  }
  return <CreateStudentDialog open={open} onOpenChange={onOpenChange} />;
}

function GenderSelect({
  value,
  onChange,
}: {
  value: string | undefined;
  onChange: (value: string) => void;
}) {
  return (
    <Select value={value ?? ""} onValueChange={(v) => onChange(v === "none" ? "" : v)}>
      <SelectTrigger className="w-full">
        <SelectValue placeholder="Not specified" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="none">Not specified</SelectItem>
        {GENDER_OPTIONS.map((option) => (
          <SelectItem key={option.value} value={option.value}>
            {option.label}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}

function CreateStudentDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const createMutation = useCreateStudent();
  const form = useForm<StudentCreateFormInput, unknown, StudentCreateFormValues>({
    resolver: zodResolver(studentCreateSchema),
    defaultValues: {
      userId: "",
      studentCode: "",
      khmerName: "",
      englishName: "",
      dob: "",
      gender: "",
      phone: "",
    },
  });

  useEffect(() => {
    if (!open) form.reset();
  }, [open, form]);

  const onSubmit = form.handleSubmit((values) => {
    createMutation.mutate(
      {
        userId: values.userId,
        studentCode: values.studentCode,
        khmerName: values.khmerName,
        englishName: values.englishName ?? null,
        dob: values.dob ?? null,
        gender: values.gender ?? null,
        phone: values.phone ?? null,
      },
      {
        onSuccess: () => {
          toast.success(`Student "${values.studentCode}" created.`);
          onOpenChange(false);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  });

  const errors = form.formState.errors;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New student</DialogTitle>
          <DialogDescription>
            Link an existing user account to a new student profile.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="create-user-id">User ID</Label>
              <Input id="create-user-id" inputMode="numeric" placeholder="e.g. 12" {...form.register("userId")} />
              {errors.userId && <p className="text-sm text-destructive">{errors.userId.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="create-student-code">Student code</Label>
              <Input
                id="create-student-code"
                placeholder="e.g. S-0006"
                maxLength={20}
                {...form.register("studentCode")}
              />
              {errors.studentCode && (
                <p className="text-sm text-destructive">{errors.studentCode.message}</p>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-khmer-name">Khmer name</Label>
            <Input id="create-khmer-name" maxLength={150} {...form.register("khmerName")} />
            {errors.khmerName && <p className="text-sm text-destructive">{errors.khmerName.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-english-name">English name</Label>
            <Input id="create-english-name" maxLength={150} {...form.register("englishName")} />
            {errors.englishName && (
              <p className="text-sm text-destructive">{errors.englishName.message}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="create-dob">Date of birth</Label>
              <Input id="create-dob" type="date" {...form.register("dob")} />
              {errors.dob && <p className="text-sm text-destructive">{errors.dob.message}</p>}
            </div>
            <div className="space-y-2">
              <Label>Gender</Label>
              <GenderSelect
                value={form.watch("gender")}
                onChange={(v) => form.setValue("gender", v, { shouldValidate: true })}
              />
              {errors.gender && <p className="text-sm text-destructive">{errors.gender.message}</p>}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-phone">Phone</Label>
            <Input id="create-phone" maxLength={20} {...form.register("phone")} />
            {errors.phone && <p className="text-sm text-destructive">{errors.phone.message}</p>}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? "Creating…" : "Create student"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function EditStudentDialog({
  open,
  onOpenChange,
  student,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  student: StudentResponse;
}) {
  const updateMutation = useUpdateStudent();
  const form = useForm<StudentUpdateFormInput, unknown, StudentUpdateFormValues>({
    resolver: zodResolver(studentUpdateSchema),
    defaultValues: {
      khmerName: student.khmerName,
      englishName: student.englishName ?? "",
      dob: student.dob ?? "",
      gender: student.gender ?? "",
      phone: student.phone ?? "",
    },
  });

  const onSubmit = form.handleSubmit((values) => {
    updateMutation.mutate(
      {
        id: student.id,
        payload: {
          khmerName: values.khmerName,
          englishName: values.englishName ?? null,
          dob: values.dob ?? null,
          gender: values.gender ?? null,
          phone: values.phone ?? null,
        },
      },
      {
        onSuccess: () => {
          toast.success(`Student "${student.studentCode}" updated.`);
          onOpenChange(false);
        },
        onError: (error) => toast.error(toApiError(error).detail),
      },
    );
  });

  const errors = form.formState.errors;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit student</DialogTitle>
          <DialogDescription>
            Update profile details. The student code and linked account can&apos;t be changed.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <div className="font-medium">Student code</div>
              <div className="text-muted-foreground">{student.studentCode}</div>
            </div>
            <div>
              <div className="font-medium">Account</div>
              <div className="text-muted-foreground">{student.username}</div>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-khmer-name">Khmer name</Label>
            <Input id="edit-khmer-name" maxLength={150} {...form.register("khmerName")} />
            {errors.khmerName && <p className="text-sm text-destructive">{errors.khmerName.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-english-name">English name</Label>
            <Input id="edit-english-name" maxLength={150} {...form.register("englishName")} />
            {errors.englishName && (
              <p className="text-sm text-destructive">{errors.englishName.message}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="edit-dob">Date of birth</Label>
              <Input id="edit-dob" type="date" {...form.register("dob")} />
              {errors.dob && <p className="text-sm text-destructive">{errors.dob.message}</p>}
            </div>
            <div className="space-y-2">
              <Label>Gender</Label>
              <GenderSelect
                value={form.watch("gender")}
                onChange={(v) => form.setValue("gender", v, { shouldValidate: true })}
              />
              {errors.gender && <p className="text-sm text-destructive">{errors.gender.message}</p>}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-phone">Phone</Label>
            <Input id="edit-phone" maxLength={20} {...form.register("phone")} />
            {errors.phone && <p className="text-sm text-destructive">{errors.phone.message}</p>}
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