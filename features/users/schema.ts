import { z } from "zod";

// Mirrors @NotBlank @Size(max = 100) username
const usernameField = z
  .string()
  .trim()
  .min(1, "Username is required")
  .max(100, "Username must be 100 characters or fewer");

// Mirrors @Email @Size(max = 255) email (optional on the backend)
const emailField = z
  .string()
  .trim()
  .max(255, "Email must be 255 characters or fewer")
  .optional()
  .transform((value) => (value ? value : undefined))
  .refine((value) => value === undefined || z.string().email().safeParse(value).success, {
    message: "Enter a valid email",
  });

// Mirrors @NotBlank @Size(min = 8, max = 100) password
const passwordField = z
  .string()
  .min(8, "Password must be at least 8 characters")
  .max(100, "Password must be 100 characters or fewer");

// Mirrors @NotEmpty roleNames
const roleNamesField = z.array(z.string()).min(1, "Select at least one role");

export const userCreateSchema = z.object({
  username: usernameField,
  email: emailField,
  password: passwordField,
  roleNames: roleNamesField,
});

export type UserCreateFormInput = z.input<typeof userCreateSchema>;
export type UserCreateFormValues = z.output<typeof userCreateSchema>;