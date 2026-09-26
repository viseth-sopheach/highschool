"use client";

import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import {
  userCreateSchema,
  type UserCreateFormInput,
  type UserCreateFormValues,
} from "@/features/users/schema";
import { useCreateUser } from "@/features/users/hooks/use-create-user";
import { USER_ROLE_NAMES, type UserRoleName } from "@/features/users/types";
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

const ROLE_LABEL: Record<UserRoleName, string> = {
  STUDENT: "Student",
  TEACHER: "Teacher",
  VICE_PRINCIPAL: "Vice Principal",
  PRINCIPAL: "Principal",
};

interface UserCreateDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function UserCreateDialog({ open, onOpenChange }: UserCreateDialogProps) {
  const createMutation = useCreateUser();
  const form = useForm<UserCreateFormInput, unknown, UserCreateFormValues>({
    resolver: zodResolver(userCreateSchema),
    defaultValues: { username: "", email: "", password: "", roleNames: [] },
  });

  useEffect(() => {
    if (!open) form.reset();
  }, [open, form]);

  const errors = form.formState.errors;
  const selectedRole = form.watch("roleNames")?.[0] ?? "";

  const selectRole = (role: UserRoleName) => {
    form.setValue("roleNames", [role], { shouldValidate: true });
  };

  const onSubmit = form.handleSubmit((values) => {
    createMutation.mutate(
      {
        username: values.username,
        email: values.email ?? null,
        password: values.password,
        roleNames: values.roleNames,
      },
      {
        onSuccess: () => {
          toast.success(`User "${values.username}" created.`);
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
          <DialogTitle>New user</DialogTitle>
          <DialogDescription>Create an account and assign its role.</DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="create-username">Username</Label>
            <Input id="create-username" maxLength={100} {...form.register("username")} />
            {errors.username && (
              <p className="text-sm text-destructive">{errors.username.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-email">Email</Label>
            <Input id="create-email" type="email" maxLength={255} {...form.register("email")} />
            {errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="create-password">Password</Label>
            <Input id="create-password" type="password" {...form.register("password")} />
            {errors.password && (
              <p className="text-sm text-destructive">{errors.password.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label>Role</Label>
            <div className="grid grid-cols-2 gap-2">
              {USER_ROLE_NAMES.map((role) => (
                <label key={role} className="flex items-center gap-2 text-sm">
                  <input
                    type="radio"
                    name="create-user-role"
                    className="size-4 border-input"
                    checked={selectedRole === role}
                    onChange={() => selectRole(role)}
                  />
                  {ROLE_LABEL[role]}
                </label>
              ))}
            </div>
            {errors.roleNames && (
              <p className="text-sm text-destructive">{errors.roleNames.message as string}</p>
            )}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? "Creating…" : "Create user"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}