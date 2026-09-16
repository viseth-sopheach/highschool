import { httpClient } from "@/lib/api/http-client";

export async function deleteSchoolClass(id: number): Promise<void> {
  await httpClient.delete(`/api/school-classes/${id}`);
}