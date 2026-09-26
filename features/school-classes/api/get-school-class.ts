import { httpClient } from "@/lib/api/http-client";
import type { SchoolClassResponse } from "@/features/school-classes/types";

export async function getSchoolClass(id: number): Promise<SchoolClassResponse> {
  const { data } = await httpClient.get<SchoolClassResponse>(`/api/school-classes/${id}`);
  return data;
}