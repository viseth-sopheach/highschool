import { httpClient } from "@/lib/api/http-client";
import type { SchoolClassCreateRequest, SchoolClassResponse } from "@/features/school-classes/types";

export async function createSchoolClass(
  payload: SchoolClassCreateRequest,
): Promise<SchoolClassResponse> {
  const { data } = await httpClient.post<SchoolClassResponse>("/api/school-classes", payload);
  return data;
}