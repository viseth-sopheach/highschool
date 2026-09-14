"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { bootstrapSession } from "@/lib/auth/bootstrap";
import { useAuthStore } from "@/lib/auth/token-store";
import { DashboardSidebar } from "@/components/dashboard/sidebar";
import { DashboardTopbar } from "@/components/dashboard/topbar";

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const accessToken = useAuthStore((s) => s.accessToken);
  const [status, setStatus] = useState<"checking" | "ready">("checking");

  useEffect(() => {
    if (accessToken) {
      setStatus("ready");
      return;
    }
    bootstrapSession().then((ok) => {
      if (!ok) {
        router.replace("/login");
        return;
      }
      setStatus("ready");
    });
  }, [accessToken, router]);

  if (status === "checking") {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">
        Loading your dashboard…
      </div>
    );
  }

  return (
    <div className="flex min-h-screen">
      <DashboardSidebar />
      <div className="flex flex-1 flex-col">
        <DashboardTopbar />
        <main className="flex-1 p-6">{children}</main>
      </div>
    </div>
  );
}