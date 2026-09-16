import { httpClient } from "@/lib/api/http-client";
import type { SchoolClassResponse, SchoolClassUpdateRequest } from "@/features/school-classes/types";

export async function updateSchoolClass(
  id: number,
  payload: SchoolClassUpdateRequest,
): Promise<SchoolClassResponse> {
  const { data } = await httpClient.put<SchoolClassResponse>(`/api/school-classes/${id}`, payload);
  return data;
}