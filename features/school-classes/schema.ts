import { z } from "zod";

const requiredSelect = (message: string) =>
  z.string().min(1, message).transform((value) => Number(value));

const optionalSelect = z
  .string()
  .optional()
  .transform((value) => (value ? Number(value) : undefined));

// Mirrors @NotBlank @Size(max = 10) on SchoolClassCreateRequest/UpdateRequest.name
const nameField = z
  .string()
  .trim()
  .min(1, "Class name is required")
  .max(10, "Class name must be 10 characters or fewer");

// Mirrors @Positive Short capacity
const capacityField = z
  .union([z.string(), z.number()])
  .optional()
  .transform((value) => {
    if (value === undefined || value === "") return undefined;
    const num = typeof value === "number" ? value : Number(value);
    return Number.isFinite(num) ? num : undefined;
  })
  .refine((value) => value === undefined || (Number.isInteger(value) && value > 0), {
    message: "Capacity must be a positive whole number",
  });

export const schoolClassCreateSchema = z.object({
  academicYearId: requiredSelect("Academic year is required"),
  gradeId: requiredSelect("Grade is required"),
  studyTrackId: optionalSelect,
  name: nameField,
  capacity: capacityField,
});

export const schoolClassUpdateSchema = z.object({
  studyTrackId: optionalSelect,
  name: nameField,
  capacity: capacityField,
});

export type SchoolClassCreateFormInput = z.input<typeof schoolClassCreateSchema>;
export type SchoolClassCreateFormValues = z.output<typeof schoolClassCreateSchema>;
export type SchoolClassUpdateFormInput = z.input<typeof schoolClassUpdateSchema>;
export type SchoolClassUpdateFormValues = z.output<typeof schoolClassUpdateSchema>;