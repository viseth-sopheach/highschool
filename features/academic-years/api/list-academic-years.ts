import { httpClient } from "@/lib/api/http-client";
import type { AcademicYearResponse } from "@/features/academic-years/types";

export async function listAcademicYears(): Promise<AcademicYearResponse[]> {
  const { data } = await httpClient.get<AcademicYearResponse[]>("/api/academic/years");
  return data;
}