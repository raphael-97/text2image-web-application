import { ServerResponse } from "@/dto/errorResponse";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { NextRequest } from "next/server";

interface ModelInput {
  inputs: string;
}

export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models/${params.id}`,
    { next: { revalidate: 0 } }
  );

  if (!res.ok) {
    const errorResponse: ResourceServerResponse = await res.json();
    throw new Error(errorResponse.message);
  }

  const blob = await res.blob();
  return new Response(blob);
}

export async function POST(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const inputs = await req.json();

  const modelInput: ModelInput = {
    inputs: inputs,
  };

  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models/${params.id}`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify(modelInput),
      }
    );

    if (!res.ok) {
      const errorResponse: ResourceServerResponse = await res.json();
      const serverResponse: ServerResponse = {
        success: false,
        message: errorResponse.message,
      };
      return Response.json(serverResponse, { status: 200 });
    }
    const blob = await res.blob();

    return new Response(blob, { status: 201 });
  } catch (error) {
    const serverResponse: ServerResponse = {
      success: false,
      message: "Resource Server not reachable, try again",
    };
    return Response.json(serverResponse, { status: 200 });
  }
}
