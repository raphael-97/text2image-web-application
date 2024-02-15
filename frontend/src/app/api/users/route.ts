import { ServerResponse } from "@/dto/errorResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { UserResponse } from "@/dto/userResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function GET() {
  const tokenValue = cookies().get("accessToken")?.value;

  // User does not have a token => no need to call auth/resource server
  if (!tokenValue) {
    redirect("/login");
  }

  const serverResponse: ServerResponse = {
    success: false,
    message: "",
  };

  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/user`,
      {
        headers: {
          Authorization: "Bearer " + tokenValue,
        },
      }
    );

    if (res.status === 401) {
      serverResponse.message = "Resource server is not available, try again";
      return Response.json(serverResponse, { status: 200 });
    }

    if (res.status === 404) {
      const errorResponse: ResourceServerResponse = await res.json();
      serverResponse.message = errorResponse.message;
      return Response.json(serverResponse, { status: 200 });
    }
    if (!res.ok) {
      serverResponse.message = "Resource server is not available, try again";
      return Response.json(serverResponse, { status: 200 });
    }

    const userData: UserResponse = await res.json();
    return Response.json(userData, { status: 302 });
  } catch (error) {
    serverResponse.message = "Resource server is not available, try again";
    return Response.json(serverResponse, { status: 200 });
  }
}
