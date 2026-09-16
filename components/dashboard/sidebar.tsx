"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "cn";
import { useAuthorities } from "@/lib/auth/roles";
import { visibleNavItems } from "@/components/dashboard/nav-items";

export function DashboardSidebar() {
  const authorities = useAuthorities();
  const pathname = usePathname();
  const items = visibleNavItems(authorities);

  return (
    <aside className="hidden w-56 shrink-0 border-r bg-sidebar text-sidebar-foreground md:flex md:flex-col">
      <div className="px-4 py-4 text-sm font-semibold">Mesang HighSchool</div>
      <nav className="flex-1 space-y-0.5 px-2">
        {items.map((item) => {
          const active = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "block rounded-lg px-3 py-2 text-sm transition-colors",
                active
                  ? "bg-sidebar-accent text-sidebar-accent-foreground"
                  : "text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
              )}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}