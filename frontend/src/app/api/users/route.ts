import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function GET() {
  const data = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/user`,
    {
      headers: {
        Authorization: "Bearer " + cookies().get("accessToken")?.value,
      },
    }
  );

  const userData = await data.json();
  return NextResponse.json(userData);
}
