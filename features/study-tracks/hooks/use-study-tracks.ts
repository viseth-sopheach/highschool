import { useQuery } from "@tanstack/react-query";
import { listStudyTracks } from "@/features/study-tracks/api/list-study-tracks";

export function useStudyTracks() {
  return useQuery({
    queryKey: ["study-tracks"],
    queryFn: listStudyTracks,
    staleTime: 5 * 60 * 1000,
  });
}