import { create } from "zustand";

interface AuthUser {
  username: string;
  authorities: string[]; // e.g. ["ROLE_TEACHER", "PERM_STUDENT_READ", ...]
}

interface AuthState {
  accessToken: string | null;
  user: AuthUser | null;
  setSession: (accessToken: string, user: AuthUser) => void;
  clearSession: () => void;
}

// In-memory only — never persisted. Lost on hard refresh by design;
// the refresh-token flow (see lib/api/http-client.ts) re-establishes it.
export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  setSession: (accessToken, user) => set({ accessToken, user }),
  clearSession: () => set({ accessToken: null, user: null }),
}));

const REFRESH_TOKEN_KEY = "hsms_refresh_token";

export const refreshTokenStorage = {
  get: () =>
    typeof window === "undefined" ? null : localStorage.getItem(REFRESH_TOKEN_KEY),
  set: (token: string) => localStorage.setItem(REFRESH_TOKEN_KEY, token),
  clear: () => localStorage.removeItem(REFRESH_TOKEN_KEY),
};