import { ServerResponse } from "@/dto/errorResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { UserResponse } from "@/dto/userResponse";
import { cookies } from "next/headers";

export async function GET() {
  const tokenValue = cookies().get("accessToken")?.value;

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
      throw new Error("User is not authorized");
    }

    if (res.status === 404) {
      const errorResponse: ResourceServerResponse = await res.json();
      throw new Error(errorResponse.message);
    }
    if (!res.ok) {
      throw new Error("Resource server is not available, try again");
    }

    const userData: UserResponse = await res.json();
    return Response.json(userData, { status: 302 });
  } catch (error) {
    throw new Error("Resource server is not available, try again");
  }
}
