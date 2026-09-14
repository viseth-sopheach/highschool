import { useRouter } from "next/navigation";
import { useAuthStore, refreshTokenStorage } from "@/lib/auth/token-store";
import { logout } from "@/features/auth/api/logout";

export function useLogout() {
  const router = useRouter();
  const clearSession = useAuthStore((s) => s.clearSession);

  return async () => {
    const refreshToken = refreshTokenStorage.get();
    if (refreshToken) {
      try {
        await logout(refreshToken);
      } catch {
        // best-effort — clear local session regardless of server outcome
      }
    }
    clearSession();
    refreshTokenStorage.clear();
    router.replace("/login");
  };
}