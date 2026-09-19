import { z } from "zod";

function todayIso(): string {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
}

const optionalText = (max: number, label: string) =>
  z
    .string()
    .trim()
    .max(max, `${label} must be ${max} characters or fewer`)
    .optional()
    .transform((value) => (value ? value : undefined));

// Mirrors @NotNull Long userId
const userIdField = z
  .string()
  .trim()
  .min(1, "User ID is required")
  .regex(/^\d+$/, "User ID must be a positive whole number")
  .transform((value) => Number(value))
  .refine((value) => value > 0, { message: "User ID must be a positive whole number" });

// Mirrors @NotBlank @Size(max = 20) studentCode
const studentCodeField = z
  .string()
  .trim()
  .min(1, "Student code is required")
  .max(20, "Student code must be 20 characters or fewer");

// Mirrors @NotBlank @Size(max = 150) khmerName
const khmerNameField = z
  .string()
  .trim()
  .min(1, "Khmer name is required")
  .max(150, "Khmer name must be 150 characters or fewer");

// Mirrors @Past LocalDate dob
const dobField = z
  .string()
  .optional()
  .refine((value) => !value || value < todayIso(), {
    message: "Date of birth must be in the past",
  })
  .transform((value) => (value ? value : undefined));

export const studentCreateSchema = z.object({
  userId: userIdField,
  studentCode: studentCodeField,
  khmerName: khmerNameField,
  englishName: optionalText(150, "English name"),
  dob: dobField,
  gender: optionalText(10, "Gender"),
  phone: optionalText(20, "Phone"),
});

export const studentUpdateSchema = z.object({
  khmerName: khmerNameField,
  englishName: optionalText(150, "English name"),
  dob: dobField,
  gender: optionalText(10, "Gender"),
  phone: optionalText(20, "Phone"),
});

export type StudentCreateFormInput = z.input<typeof studentCreateSchema>;
export type StudentCreateFormValues = z.output<typeof studentCreateSchema>;
export type StudentUpdateFormInput = z.input<typeof studentUpdateSchema>;
export type StudentUpdateFormValues = z.output<typeof studentUpdateSchema>;