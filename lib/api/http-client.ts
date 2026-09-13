import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import { useAuthStore, refreshTokenStorage } from "@/lib/auth/token-store";

export const httpClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: { "Content-Type": "application/json" },
});

// Attach the access token to every outgoing request.
httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On a 401, try exactly one silent refresh, then retry the original request.
// Concurrent 401s share a single in-flight refresh instead of firing N refreshes.
let refreshPromise: Promise<string | null> | null = null;

async function performRefresh(): Promise<string | null> {
  const refreshToken = refreshTokenStorage.get();
  if (!refreshToken) return null;

  try {
    const { data } = await axios.post(
      `${process.env.NEXT_PUBLIC_API_URL}/api/auth/refresh`,
      { refreshToken },
    );
    refreshTokenStorage.set(data.refreshToken);
    return data.accessToken as string;
  } catch {
    return null;
  }
}

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    const isAuthRoute = originalRequest.url?.includes("/api/auth/");
    if (error.response?.status !== 401 || originalRequest._retry || isAuthRoute) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;
    refreshPromise ??= performRefresh().finally(() => {
      refreshPromise = null;
    });

    const newAccessToken = await refreshPromise;
    if (!newAccessToken) {
      useAuthStore.getState().clearSession();
      refreshTokenStorage.clear();
      if (typeof window !== "undefined") window.location.href = "/login";
      return Promise.reject(error);
    }

    // We don't have the decoded user here without re-fetching; keep the
    // existing user in store and just refresh the token.
    useAuthStore.setState({ accessToken: newAccessToken });
    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
    return httpClient(originalRequest);
  },
);