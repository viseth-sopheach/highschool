import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function RegisterInfoPage() {
  return (
    <Card className="w-full max-w-sm">
      <CardHeader>
        <CardTitle>Creating an account</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <p className="text-sm text-muted-foreground">
          Student, teacher, and staff accounts are created by your school
          administrator. Self-service sign-up isn&apos;t available.
        </p>
        <p className="text-sm text-muted-foreground">
          Contact your school office to request access, or sign in below if
          you already have credentials.
        </p>
        <Button asChild className="w-full">
          <Link href="/login">Back to sign in</Link>
        </Button>
      </CardContent>
    </Card>
  );
}