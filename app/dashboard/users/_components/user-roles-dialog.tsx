"use client";

import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { USER_ROLE_NAMES, type UserResponse, type UserRoleName } from "@/features/users/types";

const ROLE_LABEL: Record<UserRoleName, string> = {
  STUDENT: "Student",
  TEACHER: "Teacher",
  VICE_PRINCIPAL: "Vice Principal",
  PRINCIPAL: "Principal",
};

interface UserRolesDialogProps {
  user: UserResponse | undefined;
  pending: boolean;
  onSave: (roleNames: string[]) => void;
  onClose: () => void;
}

export function UserRolesDialog({ user, pending, onSave, onClose }: UserRolesDialogProps) {
  const [selected, setSelected] = useState<string[]>([]);

  useEffect(() => {
    if (user) setSelected(user.roles);
  }, [user]);

  const toggleRole = (role: UserRoleName, checked: boolean) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (checked) next.add(role);
      else next.delete(role);
      return Array.from(next);
    });
  };

  return (
    <Dialog open={Boolean(user)} onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit roles</DialogTitle>
          <DialogDescription>
            {user && <>Choose which roles &quot;{user.username}&quot; should have.</>}
          </DialogDescription>
        </DialogHeader>

        <div className="grid grid-cols-2 gap-2">
          {USER_ROLE_NAMES.map((role) => (
            <label key={role} className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                className="size-4 rounded border-input"
                checked={selected.includes(role)}
                onChange={(e) => toggleRole(role, e.target.checked)}
              />
              {ROLE_LABEL[role]}
            </label>
          ))}
        </div>
        {selected.length === 0 && (
          <p className="text-sm text-destructive">Select at least one role.</p>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={() => onSave(selected)} disabled={pending || selected.length === 0}>
            {pending ? "Saving…" : "Save roles"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}