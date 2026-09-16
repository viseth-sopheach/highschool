import { httpClient } from "@/lib/api/http-client";
import type { GradeResponse } from "@/features/grades/types";

export async function listGrades(): Promise<GradeResponse[]> {
  const { data } = await httpClient.get<GradeResponse[]>("/api/academic/grades");
  return data;
}