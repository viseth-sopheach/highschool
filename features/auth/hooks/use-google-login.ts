import { useMutation } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import { googleLogin, type GoogleLoginRequest } from "@/features/auth/api/google-login";
import { useAuthStore, refreshTokenStorage } from "@/lib/auth/token-store";

interface AccessTokenClaims {
  sub: string;
  authorities: string[];
}

export function useGoogleLogin() {
  const setSession = useAuthStore((s) => s.setSession);

  return useMutation({
    mutationFn: (payload: GoogleLoginRequest) => googleLogin(payload),
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