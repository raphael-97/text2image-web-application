import { ServerResponse } from "@/dto/errorResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { cookies } from "next/headers";
import { NextRequest } from "next/server";

export async function GET() {
  const data = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image`,
    {
      headers: {
        Authorization: "Bearer " + cookies().get("accessToken")?.value,
      },
    }
  );
  if (!data.ok) {
    throw new Error("Failed to fetch user's images");
  }

  return data;
}

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

  const serverResponse: ServerResponse = {
    success: false,
    message: "",
  };

  if (res.status === 401) {
    serverResponse.message = "Login to upload images to the gallery";
    return Response.json(serverResponse, { status: 200 });
  }

  if (!res.ok) {
    const errorResponse: ResourceServerResponse = await res.json();
    serverResponse.message = errorResponse.message;
    return Response.json(serverResponse, { status: 200 });
  }

  serverResponse.success = true;
  serverResponse.message = "Uploading to gallery successful";
  return Response.json(serverResponse, { status: 200 });
}
