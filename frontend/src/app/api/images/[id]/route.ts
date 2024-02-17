import { CheckIfUserHasAccessToImage } from "@/app/lib/imageAuthActions";
import { NextRequest } from "next/server";

export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const blob = await CheckIfUserHasAccessToImage(parseInt(params.id));
  return new Response(blob);
}
