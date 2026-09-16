import { httpClient } from "@/lib/api/http-client";
import type { StudyTrackResponse } from "@/features/study-tracks/types";

export async function listStudyTracks(): Promise<StudyTrackResponse[]> {
  const { data } = await httpClient.get<StudyTrackResponse[]>("/api/academic/study-tracks");
  return data;
}