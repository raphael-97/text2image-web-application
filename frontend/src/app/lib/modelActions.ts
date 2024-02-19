"use server";

import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { ModelRequest } from "@/dto/modelRequest";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";

export async function createModelAction(prevState: any, formData: FormData) {
  const accessToken = cookies().get("accessToken")?.value;

  if (!accessToken) {
    redirect(`${process.env.NEXT_PUBLIC_FRONTEND_DOMAIN_URL}/login`);
  }

  const modelRequest: ModelRequest = {
    name: formData.get("name"),
    inferenceUrl: formData.get("inferenceUrl"),
  };

  const sendFormData = new FormData();

  const file = formData.get("file");

  if (file instanceof File) {
    sendFormData.append("file", file, file.name);
  }

  sendFormData.append(
    "modelRequest",
    new Blob([JSON.stringify(modelRequest)], { type: "application/json" })
  );

  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      method: "POST",
      body: sendFormData,
    }
  );

  if (res.status === 401) {
    throw new Error("User is not an admin");
  }
  if (!res.ok) {
    const errorJson = await res.json();
    const errorResponse: ResourceServerResponse = {
      ...errorJson,
    };
    return errorResponse.message;
  }

  return "success";
}
