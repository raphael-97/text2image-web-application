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
  return res;
}

export async function POST(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const inputs = await req.json();

  const modelInput: ModelInput = {
    inputs: inputs,
  };

  console.log(modelInput.inputs);

  console.log("Start generating image");
  const imageData = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models/${params.id}`,
    {
      headers: {
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(modelInput),
    }
  );

  console.log("Image done");

  return imageData;
}
