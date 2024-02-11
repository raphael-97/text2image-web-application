import { ErrorResponse } from "@/dto/errorResponse";
import { ModelResponse } from "@/dto/modelResponse";

export async function GET() {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models`,
    { next: { revalidate: 10 } }
  );

  const data = await res.json();
  if (!res.ok) {
    const errorResponse: ErrorResponse = {
      ...data,
    };
    return errorResponse;
  }

  const modelResponse: ModelResponse[] = data;
  return modelResponse;
}
