// Mirrors com.seth.backend.teacher.dto.* exactly.
export interface TeacherResponse {
  id: number;
  userId: number;
  username: string;
  teacherCode: string;
  khmerName: string;
  englishName: string | null;
  phone: string | null;
  hireDate: string | null; // ISO yyyy-MM-dd
}

export interface TeacherCreateRequest {
  userId: number;
  teacherCode: string;
  khmerName: string;
  englishName?: string | null;
  phone?: string | null;
  hireDate?: string | null;
}

export interface TeacherUpdateRequest {
  khmerName: string;
  englishName?: string | null;
  phone?: string | null;
  hireDate?: string | null;
}

export interface TeacherListParams {
  search?: string;
  page?: number;
  size?: number;
}