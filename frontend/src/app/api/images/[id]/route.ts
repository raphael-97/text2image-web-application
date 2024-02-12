import { cookies } from "next/headers";
import { NextRequest } from "next/server";

export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const imageData = fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image/${params.id}`,
    {
      headers: {
        Authorization: "Bearer " + cookies().get("accessToken")?.value,
      },
    }
  );
  return imageData;
}
