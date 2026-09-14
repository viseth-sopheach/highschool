// Mirrors com.seth.backend.auth.dto exactly.
export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresInMs: number;
}

export interface GoogleLoginRequest {
  idToken: string;
}