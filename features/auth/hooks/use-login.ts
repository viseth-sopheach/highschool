import { useMutation } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import { login } from "@/features/auth/api/login";
import { useAuthStore, refreshTokenStorage } from "@/lib/auth/token-store";
import type { LoginRequest } from "@/features/auth/types";

interface AccessTokenClaims {
  sub: string;
  authorities: string[];
}

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (payload: LoginRequest) => login(payload),
    onSuccess: (data) => {
      const claims = jwtDecode<AccessTokenClaims>(data.accessToken);
      setSession(data.accessToken, {
        username: claims.sub,
        authorities: claims.authorities,
      });
      refreshTokenStorage.set(data.refreshToken);
    },
  });
}