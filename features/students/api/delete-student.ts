import { httpClient } from "@/lib/api/http-client";

export async function deleteStudent(id: number): Promise<void> {
  await httpClient.delete(`/api/students/${id}`);
}