import { ServerResponse } from "@/dto/errorResponse";
import { ImgResponse } from "@/dto/imgResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { NextRequest } from "next/server";

export async function GET() {
  const tokenValue = cookies().get("accessToken")?.value;

  // User does not have a token => no need to call auth/resource server
  if (!tokenValue) {
    redirect("/login");
  }

  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image`,
      {
        headers: {
          Authorization: "Bearer " + tokenValue,
        },
      }
    );

    // User has wrong or manipulated token
    if (res.status === 401) {
      redirect("/login");
    }

    if (!res.ok) {
      const errorResponse: ResourceServerResponse = await res.json();
      throw new Error(errorResponse.message);
    }

    const data: ImgResponse[] = await res.json();
    return Response.json(data);
  } catch (error) {
    throw new Error("Resource Server not reachable, try again");
  }
}

export async function POST(req: NextRequest) {
  const formData = await req.formData();
  const tokenValue = cookies().get("accessToken")?.value;

  const serverResponse: ServerResponse = {
    success: false,
    message: "",
  };

  if (!tokenValue) {
    serverResponse.message = "Login to upload images to the gallery";
    return Response.json(serverResponse, { status: 200 });
  }

  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image`,
      {
        headers: {
          Authorization: "Bearer " + tokenValue,
        },
        method: "POST",
        body: formData,
      }
    );

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
  } catch (error) {
    serverResponse.message = "Resource Server not reachable, try again";
    return Response.json(serverResponse, { status: 200 });
  }
}
