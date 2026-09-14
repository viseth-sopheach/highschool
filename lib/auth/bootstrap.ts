import { jwtDecode } from "jwt-decode";
import { useAuthStore, refreshTokenStorage } from "@/lib/auth/token-store";
import { refreshSession } from "@/features/auth/api/refresh";

interface AccessTokenClaims {
  sub: string;
  authorities: string[];
}

/**
 * The access token lives only in memory (lib/auth/token-store.ts), so a hard
 * refresh or new tab always starts with an empty store. This exchanges the
 * persisted refresh token for a fresh access token before any protected
 * route renders — returns false if there's no valid session to restore.
 */
export async function bootstrapSession(): Promise<boolean> {
  if (useAuthStore.getState().accessToken) return true;

  const refreshToken = refreshTokenStorage.get();
  if (!refreshToken) return false;

  try {
    const data = await refreshSession(refreshToken);
    const claims = jwtDecode<AccessTokenClaims>(data.accessToken);
    useAuthStore.getState().setSession(data.accessToken, {
      username: claims.sub,
      authorities: claims.authorities,
    });
    refreshTokenStorage.set(data.refreshToken);
    return true;
  } catch {
    refreshTokenStorage.clear();
    return false;
  }
}