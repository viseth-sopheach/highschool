// Mirrors com.seth.backend.student.dto.* exactly.
export interface StudentResponse {
  id: number;
  userId: number;
  username: string;
  studentCode: string;
  khmerName: string;
  englishName: string | null;
  dob: string | null; // ISO yyyy-MM-dd
  gender: string | null;
  phone: string | null;
}

export interface StudentCreateRequest {
  userId: number;
  studentCode: string;
  khmerName: string;
  englishName?: string | null;
  dob?: string | null;
  gender?: string | null;
  phone?: string | null;
}

export interface StudentUpdateRequest {
  khmerName: string;
  englishName?: string | null;
  dob?: string | null;
  gender?: string | null;
  phone?: string | null;
}

export interface StudentListParams {
  search?: string;
  page?: number;
  size?: number;
}