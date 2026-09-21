// Mirrors com.seth.backend.assignment.dto.ClassTeacherAssignmentResponse.
export interface ClassTeacherAssignmentResponse {
  id: number;
  schoolClassId: number;
  teacherId: number;
  teacherName: string;
  subjectId: number | null;
  subjectName: string | null;
  homeroom: boolean;
}