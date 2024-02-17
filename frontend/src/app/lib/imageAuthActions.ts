"use server";

import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { cookies } from "next/headers";

export async function CheckIfUserHasAccessToImage(id: number) {
  const tokenValue = cookies().get("accessToken")?.value;

  // User does not have a token => no need to call auth/resource server
  if (!tokenValue) {
    Response.redirect("/login");
  }

  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/image/${id}`,
    {
      headers: {
        Authorization: "Bearer " + tokenValue,
      },
    }
  );

  if (res.status === 401) {
    throw new Error("User is unauthorized to access this resource");
  }

  if (!res.ok) {
    const errorResponse: ResourceServerResponse = await res.json();
    throw new Error(errorResponse.message);
  }

  const blob = await res.blob();
  return blob;
}
