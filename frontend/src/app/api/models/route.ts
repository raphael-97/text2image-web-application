import { ModelResponse } from "@/dto/modelResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { NextResponse } from "next/server";

export async function GET() {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models`,
    { next: { revalidate: 0 } }
  );

  if (!res.ok) {
    const errorResponse: ResourceServerResponse = await res.json();
    throw new Error(errorResponse.message);
  }

  const modelData: ModelResponse[] = await res.json();
  return NextResponse.json(modelData);
}
