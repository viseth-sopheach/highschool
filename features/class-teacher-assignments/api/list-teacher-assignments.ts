import { httpClient } from "@/lib/api/http-client";
import type { ClassTeacherAssignmentResponse } from "@/features/class-teacher-assignments/types";

export async function listTeacherAssignments(
  teacherId: number,
): Promise<ClassTeacherAssignmentResponse[]> {
  const { data } = await httpClient.get<ClassTeacherAssignmentResponse[]>(
    `/api/teachers/${teacherId}/assignments`,
  );
  return data;
}