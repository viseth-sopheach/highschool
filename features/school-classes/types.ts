// Mirrors com.seth.backend.schoolclass.dto.* exactly.
export interface SchoolClassResponse {
  id: number;
  academicYearId: number;
  academicYearName: string;
  gradeId: number;
  gradeName: string;
  studyTrackId: number | null;
  studyTrackName: string | null;
  name: string;
  capacity: number | null;
}

export interface SchoolClassCreateRequest {
  academicYearId: number;
  gradeId: number;
  studyTrackId?: number | null;
  name: string;
  capacity?: number | null;
}

export interface SchoolClassUpdateRequest {
  name: string;
  studyTrackId?: number | null;
  capacity?: number | null;
}

export interface SchoolClassListParams {
  academicYearId?: number;
  gradeId?: number;
  search?: string;
  page?: number;
  size?: number;
}