import { z } from "zod";

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

// Mirrors @NotBlank @Size(max = 20) teacherCode
const teacherCodeField = z
  .string()
  .trim()
  .min(1, "Teacher code is required")
  .max(20, "Teacher code must be 20 characters or fewer");

// Mirrors @NotBlank @Size(max = 150) khmerName
const khmerNameField = z
  .string()
  .trim()
  .min(1, "Khmer name is required")
  .max(150, "Khmer name must be 150 characters or fewer");

// hireDate is an unconstrained optional LocalDate on the backend
const hireDateField = z
  .string()
  .optional()
  .transform((value) => (value ? value : undefined));

export const teacherCreateSchema = z.object({
  userId: userIdField,
  teacherCode: teacherCodeField,
  khmerName: khmerNameField,
  englishName: optionalText(150, "English name"),
  phone: optionalText(20, "Phone"),
  hireDate: hireDateField,
});

export const teacherUpdateSchema = z.object({
  khmerName: khmerNameField,
  englishName: optionalText(150, "English name"),
  phone: optionalText(20, "Phone"),
  hireDate: hireDateField,
});

export type TeacherCreateFormInput = z.input<typeof teacherCreateSchema>;
export type TeacherCreateFormValues = z.output<typeof teacherCreateSchema>;
export type TeacherUpdateFormInput = z.input<typeof teacherUpdateSchema>;
export type TeacherUpdateFormValues = z.output<typeof teacherUpdateSchema>;