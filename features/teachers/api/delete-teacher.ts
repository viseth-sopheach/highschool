import { httpClient } from "@/lib/api/http-client";

export async function deleteTeacher(id: number): Promise<void> {
  await httpClient.delete(`/api/teachers/${id}`);
}