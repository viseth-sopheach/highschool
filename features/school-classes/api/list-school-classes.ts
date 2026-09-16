import { httpClient } from "@/lib/api/http-client";
import type { PageResponse } from "@/lib/api/types";
import type { SchoolClassListParams, SchoolClassResponse } from "@/features/school-classes/types";

export async function listSchoolClasses(
  params: SchoolClassListParams,
): Promise<PageResponse<SchoolClassResponse>> {
  const { data } = await httpClient.get<PageResponse<SchoolClassResponse>>("/api/school-classes", {
    params,
  });
  return data;
}