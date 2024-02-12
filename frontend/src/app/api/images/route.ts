import { cookies } from "next/headers";
import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  const formData = await req.formData();

  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image`,
    {
      headers: {
        Authorization: "Bearer " + cookies().get("accessToken")?.value,
      },
      method: "POST",
      body: formData,
    }
  );

  return new NextResponse(JSON.stringify({ status: res.status }));
}
