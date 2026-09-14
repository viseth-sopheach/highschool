"use client";

import { useEffect, useRef, useState } from "react";
import Script from "next/script";
import { useGoogleLogin } from "@/features/auth/hooks/use-google-login";

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: Record<string, unknown>) => void;
          renderButton: (parent: HTMLElement, options: Record<string, unknown>) => void;
        };
      };
    };
  }
}

interface GoogleSignInButtonProps {
  onSuccess: () => void;
  onError: (message: string) => void;
}

export function GoogleSignInButton({ onSuccess, onError }: GoogleSignInButtonProps) {
  const buttonRef = useRef<HTMLDivElement>(null);
  const [scriptLoaded, setScriptLoaded] = useState(false);
  const googleLoginMutation = useGoogleLogin();
  const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

  useEffect(() => {
    if (!scriptLoaded || !clientId || !buttonRef.current || !window.google) return;

    window.google.accounts.id.initialize({
      client_id: clientId,
      callback: (response: { credential: string }) => {
        googleLoginMutation.mutate(
          { idToken: response.credential },
          {
            onSuccess,
            onError: (error) => {
              const message = error instanceof Error ? error.message : "Google sign-in failed.";
              onError(message);
            },
          },
        );
      },
    });

    window.google.accounts.id.renderButton(buttonRef.current, {
      theme: "outline",
      size: "large",
      width: 320,
      text: "continue_with",
    });
  }, [scriptLoaded, clientId, googleLoginMutation, onSuccess, onError]);

  if (!clientId) {
    return (
      <button
        type="button"
        disabled
        title="Google sign-in is not configured (missing NEXT_PUBLIC_GOOGLE_CLIENT_ID)"
        className="flex w-full items-center justify-center gap-2 rounded-lg border border-input bg-transparent px-2.5 py-2 text-sm font-medium text-muted-foreground opacity-50"
      >
        <span className="flex size-4 items-center justify-center rounded-full border text-[10px] font-bold">
          G
        </span>
        Continue with Google
      </button>
    );
  }

  return (
    <>
      <Script
        src="https://accounts.google.com/gsi/client"
        strategy="afterInteractive"
        onLoad={() => setScriptLoaded(true)}
      />
      <div ref={buttonRef} className="flex w-full justify-center" />
    </>
  );
}