// Mirrors com.seth.backend.student.dto.StudentEnrollmentResponse.
export type EnrollmentStatus = "ACTIVE" | "TRANSFERRED" | "WITHDRAWN" | "GRADUATED";

export interface StudentEnrollmentResponse {
  id: number;
  studentId: number;
  studentCode: string;
  schoolClassId: number;
  schoolClassName: string;
  academicYearId: number;
  academicYearName: string;
  enrollmentDate: string;
  status: EnrollmentStatus;
}

export interface EnrollmentListParams {
  page?: number;
  size?: number;
}

export const ENROLLMENT_STATUSES: EnrollmentStatus[] = [
  "ACTIVE",
  "TRANSFERRED",
  "WITHDRAWN",
  "GRADUATED",
];