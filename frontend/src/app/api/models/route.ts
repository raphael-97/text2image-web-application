import { ErrorResponse } from "@/dto/errorResponse";
import { ModelResponse } from "@/dto/modelResponse";
import { NextResponse } from "next/server";

export async function GET() {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models`,
    { next: { revalidate: 0 } }
  );

  const data = await res.json();
  if (!res.ok) {
    return NextResponse.json(data);
  }
  return NextResponse.json(data);
}
