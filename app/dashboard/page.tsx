"use client";

import Link from "next/link";
import { useAuthorities } from "@/lib/auth/roles";
import { visibleNavItems } from "@/components/dashboard/nav-items";
import { Card, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";

export default function DashboardPage() {
  const authorities = useAuthorities();
  const items = visibleNavItems(authorities).filter((i) => i.href !== "/dashboard");

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Dashboard</h1>
        <p className="text-sm text-muted-foreground">
          Quick access to what you're authorized to manage.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {items.map((item) => (
          <Link key={item.href} href={item.href}>
            <Card className="transition-colors hover:bg-muted/50">
              <CardHeader>
                <CardTitle>{item.label}</CardTitle>
                <CardDescription>Go to {item.label.toLowerCase()}</CardDescription>
              </CardHeader>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}