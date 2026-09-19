"use client";

import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import {
  teacherCreateSchema,
  teacherUpdateSchema,
  type TeacherCreateFormInput,
  type TeacherCreateFormValues,
  type TeacherUpdateFormInput,
  type TeacherUpdateFormValues,
} from "@/features/teachers/schema";
import { useCreateTeacher } from "@/features/teachers/hooks/use-create-teacher";
import { useUpdateTeacher } from "@/features/teachers/hooks/use-update-teacher";
import type { TeacherResponse } from "@/features/teachers/types";
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

interface TeacherFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  teacher?: TeacherResponse;
}

export function TeacherFormDialog({ open, onOpenChange, teacher }: TeacherFormDialogProps) {
  if (teacher) {
    return <EditTeacherDialog key={teacher.id} open={open} onOpenChange={onOpenChange} teacher={teacher} />;
  }
  return <CreateTeacherDialog open={open} onOpenChange={onOpenChange} />;
}

function CreateTeacherDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const createMutation = useCreateTeacher();
  const form = useForm<TeacherCreateFormInput, unknown, TeacherCreateFormValues>({
    resolver: zodResolver(teacherCreateSchema),
    defaultValues: {
      userId: "",
      teacherCode: "",
      khmerName: "",
      englishName: "",
      phone: "",
      hireDate: "",
    },
  });

  useEffect(() => {
    if (!open) form.reset();
  }, [open, form]);

  const onSubmit = form.handleSubmit((values) => {
    createMutation.mutate(
      {
        userId: values.userId,
        teacherCode: values.teacherCode,
        khmerName: values.khmerName,
        englishName: values.englishName ?? null,
        phone: values.phone ?? null,
        hireDate: values.hireDate ?? null,
      },
      {
        onSuccess: () => {
          toast.success(`Teacher "${values.teacherCode}" created.`);
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
          <DialogTitle>New teacher</DialogTitle>
          <DialogDescription>
            Link an existing user account to a new teacher profile.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="create-user-id">User ID</Label>
              <Input id="create-user-id" inputMode="numeric" placeholder="e.g. 8" {...form.register("userId")} />
              {errors.userId && <p className="text-sm text-destructive">{errors.userId.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="create-teacher-code">Teacher code</Label>
              <Input
                id="create-teacher-code"
                placeholder="e.g. T-0004"
                maxLength={20}
                {...form.register("teacherCode")}
              />
              {errors.teacherCode && (
                <p className="text-sm text-destructive">{errors.teacherCode.message}</p>
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
              <Label htmlFor="create-phone">Phone</Label>
              <Input id="create-phone" maxLength={20} {...form.register("phone")} />
              {errors.phone && <p className="text-sm text-destructive">{errors.phone.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="create-hire-date">Hire date</Label>
              <Input id="create-hire-date" type="date" {...form.register("hireDate")} />
              {errors.hireDate && <p className="text-sm text-destructive">{errors.hireDate.message}</p>}
            </div>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? "Creating…" : "Create teacher"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function EditTeacherDialog({
  open,
  onOpenChange,
  teacher,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  teacher: TeacherResponse;
}) {
  const updateMutation = useUpdateTeacher();
  const form = useForm<TeacherUpdateFormInput, unknown, TeacherUpdateFormValues>({
    resolver: zodResolver(teacherUpdateSchema),
    defaultValues: {
      khmerName: teacher.khmerName,
      englishName: teacher.englishName ?? "",
      phone: teacher.phone ?? "",
      hireDate: teacher.hireDate ?? "",
    },
  });

  const onSubmit = form.handleSubmit((values) => {
    updateMutation.mutate(
      {
        id: teacher.id,
        payload: {
          khmerName: values.khmerName,
          englishName: values.englishName ?? null,
          phone: values.phone ?? null,
          hireDate: values.hireDate ?? null,
        },
      },
      {
        onSuccess: () => {
          toast.success(`Teacher "${teacher.teacherCode}" updated.`);
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
          <DialogTitle>Edit teacher</DialogTitle>
          <DialogDescription>
            Update profile details. The teacher code and linked account can&apos;t be changed.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <div className="font-medium">Teacher code</div>
              <div className="text-muted-foreground">{teacher.teacherCode}</div>
            </div>
            <div>
              <div className="font-medium">Account</div>
              <div className="text-muted-foreground">{teacher.username}</div>
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
              <Label htmlFor="edit-phone">Phone</Label>
              <Input id="edit-phone" maxLength={20} {...form.register("phone")} />
              {errors.phone && <p className="text-sm text-destructive">{errors.phone.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-hire-date">Hire date</Label>
              <Input id="edit-hire-date" type="date" {...form.register("hireDate")} />
              {errors.hireDate && <p className="text-sm text-destructive">{errors.hireDate.message}</p>}
            </div>
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